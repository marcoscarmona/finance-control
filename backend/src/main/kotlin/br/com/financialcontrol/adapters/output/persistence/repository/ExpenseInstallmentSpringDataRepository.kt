package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.ExpenseInstallmentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface ExpenseInstallmentSpringDataRepository : JpaRepository<ExpenseInstallmentJpaEntity, UUID> {
    fun findAllByExpenseId(expenseId: UUID): List<ExpenseInstallmentJpaEntity>

    fun findAllByDueDateBetween(
        start: LocalDate,
        end: LocalDate,
    ): List<ExpenseInstallmentJpaEntity>
}
