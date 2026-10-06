package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CardTotal
import br.com.financialcontrol.application.dto.CategoryTotal
import br.com.financialcontrol.application.dto.MonthlyReport
import br.com.financialcontrol.application.dto.SubscriptionForecast
import br.com.financialcontrol.application.dto.UpcomingInstallment
import br.com.financialcontrol.application.dto.UpcomingInstallmentsByCard
import br.com.financialcontrol.application.port.input.GenerateMonthlyReportUseCase
import br.com.financialcontrol.application.port.output.CardInvoiceManualTotalPersistencePort
import br.com.financialcontrol.application.port.output.CategoryPersistencePort
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.application.port.output.ExpenseInstallmentPersistencePort
import br.com.financialcontrol.application.port.output.ExpensePersistencePort
import br.com.financialcontrol.application.port.output.SubscriptionPersistencePort
import br.com.financialcontrol.application.port.output.UserPersistencePort
import java.math.BigDecimal
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
        val projectionEnd = YearMonth.of(YearMonth.now().year + 1, 12).atEndOfMonth()
        val future =
            installments.findAllByUserIdAndDueDateBetween(
                userId,
                month.plusMonths(1).atDay(1),
                projectionEnd,
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
                val amountDue = manualTotal ?: detailedTotal
                val adjustment = amountDue.subtract(detailedTotal)
                CardTotal(
                    card.id,
                    card.name,
                    amountDue,
                    detailedTotal,
                    manualTotal,
                    adjustment,
                    amountDue,
                    detailedTotal,
                    adjustment.min(BigDecimal.ZERO).abs(),
                    adjustment.max(BigDecimal.ZERO),
                    future.filter { expensesById[it.expenseId]?.creditCardId == card.id }.sumOf { it.amount },
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
        val upcomingByCard =
            future
                .mapNotNull { installment ->
                    val expense = expensesById[installment.expenseId] ?: return@mapNotNull null
                    val cardId = expense.creditCardId ?: return@mapNotNull null
                    val card = cards.findById(cardId) ?: return@mapNotNull null
                    UpcomingInstallment(
                        cardId,
                        card.name,
                        expense.merchant ?: expense.description,
                        categories.findById(expense.categoryId)?.name ?: "Sem categoria",
                        installment.number,
                        installment.total,
                        installment.amount,
                        installment.dueDate,
                    )
                }.groupBy { it.cardId }
                .map { (cardId, rows) ->
                    UpcomingInstallmentsByCard(cardId, rows.first().cardName, rows.sumOf { it.amount }, rows.sortedBy { it.dueDate })
                }.sortedBy { it.cardName }
        return MonthlyReport(totalExpenses, categoriesTotal, cardTotals, forecast, upcomingByCard)
    }
}
