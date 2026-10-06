package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CardTotal
import br.com.financialcontrol.application.dto.CategoryTotal
import br.com.financialcontrol.application.dto.MonthlyReport
import br.com.financialcontrol.application.dto.SubscriptionForecast
import br.com.financialcontrol.application.port.input.GenerateMonthlyReportUseCase
import br.com.financialcontrol.application.port.output.CardInvoiceManualTotalPersistencePort
import br.com.financialcontrol.application.port.output.CategoryPersistencePort
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.application.port.output.ExpenseInstallmentPersistencePort
import br.com.financialcontrol.application.port.output.ExpensePersistencePort
import br.com.financialcontrol.application.port.output.SubscriptionPersistencePort
import br.com.financialcontrol.application.port.output.UserPersistencePort
import java.time.YearMonth
import java.util.UUID

class MonthlyReportApplicationService(
    private val users: UserPersistencePort,
    private val installments: ExpenseInstallmentPersistencePort,
    private val expenses: ExpensePersistencePort,
    private val categories: CategoryPersistencePort,
    private val cards: CreditCardPersistencePort,
    private val subscriptions: SubscriptionPersistencePort,
    private val manualTotals: CardInvoiceManualTotalPersistencePort,
) : GenerateMonthlyReportUseCase {
    override fun execute(
        userId: UUID,
        month: YearMonth,
    ): MonthlyReport {
        requireUser(users, userId)
        val items = installments.findAllByUserIdAndDueDateBetween(userId, month.atDay(1), month.atEndOfMonth())
        val expensesById = expenses.findAllByUserId(userId).associateBy { it.id }
        val categoriesTotal =
            items
                .groupBy { expensesById[it.expenseId]?.categoryId }
                .mapNotNull { (categoryId, rows) ->
                    categoryId?.let {
                        CategoryTotal(
                            categories.findById(it)?.name ?: "Unknown",
                            rows.sumOf { row -> row.amount },
                        )
                    }
                }.sortedByDescending { it.total }
        val future =
            installments.findAllByUserIdAndDueDateBetween(
                userId,
                month.plusMonths(1).atDay(1),
                month.plusMonths(3).atEndOfMonth(),
            )
        val cardTotals =
            cards.findAllByUserId(userId).map { card ->
                val cardItems = items.filter { expensesById[it.expenseId]?.creditCardId == card.id }
                val detailedTotal = cardItems.sumOf { it.amount }
                val manualTotal =
                    if (month == YearMonth.now()) {
                        manualTotals.findByCardIdAndReferenceMonth(card.id, month)?.totalAmount
                    } else {
                        null
                    }
                CardTotal(
                    card.id,
                    card.name,
                    manualTotal ?: detailedTotal,
                    detailedTotal,
                    manualTotal,
                    (manualTotal ?: detailedTotal).subtract(detailedTotal),
                    cardItems
                        .groupBy { expensesById[it.expenseId]?.categoryId }
                        .mapNotNull { (categoryId, rows) ->
                            categoryId?.let { CategoryTotal(categories.findById(it)?.name ?: "Unknown", rows.sumOf { row -> row.amount }) }
                        }.sortedByDescending { it.total },
                )
            }
        val forecast =
            subscriptions
                .findAllByUserId(userId)
                .filter { it.active }
                .map { subscription ->
                    val paymentSource =
                        subscription.creditCardId?.let { cardId -> cards.findById(cardId)?.name ?: "Cartão" }
                            ?: "Conta"
                    SubscriptionForecast(
                        subscription.name,
                        subscription.amount,
                        subscription.frequency,
                        subscription.chargeDay,
                        categories.findById(subscription.categoryId)?.name ?: "Sem categoria",
                        paymentSource,
                    )
                }.sortedBy { it.chargeDay }
        val nonCardTotal = items.filter { expensesById[it.expenseId]?.creditCardId == null }.sumOf { it.amount }
        val totalExpenses = nonCardTotal.add(cardTotals.sumOf { it.total })
        return MonthlyReport(totalExpenses, categoriesTotal, cardTotals, forecast, future)
    }
}
