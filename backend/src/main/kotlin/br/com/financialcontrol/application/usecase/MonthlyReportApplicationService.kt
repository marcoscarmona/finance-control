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
                val detailedTotal = items.filter { expensesById[it.expenseId]?.creditCardId == card.id }.sumOf { it.amount }
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
                )
            }
        val forecast =
            subscriptions
                .findAllByUserId(userId)
                .filter { it.active }
                .map { SubscriptionForecast(it.name, it.amount, it.frequency) }
        val nonCardTotal = items.filter { expensesById[it.expenseId]?.creditCardId == null }.sumOf { it.amount }
        val totalExpenses = nonCardTotal.add(cardTotals.sumOf { it.total })
        return MonthlyReport(totalExpenses, categoriesTotal, cardTotals, forecast, future)
    }
}
