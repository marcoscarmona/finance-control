package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.ExpenseInstallmentJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.ExpenseInstallmentSpringDataRepository
import br.com.financialcontrol.adapters.output.persistence.repository.ExpenseSpringDataRepository
import br.com.financialcontrol.application.port.output.ExpenseInstallmentPersistencePort
import br.com.financialcontrol.domain.model.ExpenseInstallment
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.UUID

@Component
class ExpenseInstallmentPersistenceAdapter(
    private val repository: ExpenseInstallmentSpringDataRepository,
    private val expenses: ExpenseSpringDataRepository,
) : ExpenseInstallmentPersistencePort {
    override fun saveAll(items: List<ExpenseInstallment>) {
        repository.saveAll(
            items.map {
                ExpenseInstallmentJpaEntity(
                    it.id,
                    it.expenseId,
                    it.invoiceId,
                    it.number,
                    it.total,
                    it.amount,
                    it.dueDate,
                    it.status,
                )
            },
        )
    }

    override fun findAllByExpenseId(expenseId: UUID) = repository.findAllByExpenseId(expenseId).map { it.toDomain() }

    override fun findAllByUserIdAndDueDateBetween(
        userId: UUID,
        start: LocalDate,
        end: LocalDate,
    ): List<ExpenseInstallment> {
        val ids = expenses.findAllByUserId(userId).map { it.id }.toSet()
        return repository.findAllByDueDateBetween(start, end).filter { it.expenseId in ids }.map { it.toDomain() }
    }
}

private fun ExpenseInstallmentJpaEntity.toDomain() = ExpenseInstallment(id, expenseId, invoiceId, number, total, amount, dueDate, status)
