package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CardTotal
import br.com.financialcontrol.application.dto.CategoryTotal
import br.com.financialcontrol.application.dto.MonthlyReport
import br.com.financialcontrol.application.dto.SubscriptionForecast
import br.com.financialcontrol.application.port.input.GenerateMonthlyReportUseCase
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
                CardTotal(
                    card.id,
                    card.name,
                    items.filter { expensesById[it.expenseId]?.creditCardId == card.id }.sumOf { it.amount },
                )
            }
        val forecast =
            subscriptions
                .findAllByUserId(userId)
                .filter { it.active }
                .map { SubscriptionForecast(it.name, it.amount, it.frequency) }
        return MonthlyReport(items.sumOf { it.amount }, categoriesTotal, cardTotals, forecast, future)
    }
}
