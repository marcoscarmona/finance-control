package br.com.financialcontrol.application.port.output

import br.com.financialcontrol.domain.model.ExpenseInstallment
import java.time.LocalDate
import java.util.UUID

interface ExpenseInstallmentPersistencePort {
    fun saveAll(items: List<ExpenseInstallment>)

    fun findAllByExpenseId(expenseId: UUID): List<ExpenseInstallment>

    fun findAllByUserIdAndDueDateBetween(
        userId: UUID,
        start: LocalDate,
        end: LocalDate,
    ): List<ExpenseInstallment>
}
