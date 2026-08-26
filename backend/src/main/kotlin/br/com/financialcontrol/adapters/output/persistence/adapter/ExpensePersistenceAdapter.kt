package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.ExpenseJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.ExpenseSpringDataRepository
import br.com.financialcontrol.application.port.output.ExpensePersistencePort
import br.com.financialcontrol.domain.model.Expense
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class ExpensePersistenceAdapter(
    private val repository: ExpenseSpringDataRepository,
) : ExpensePersistencePort {
    override fun save(item: Expense) =
        repository
            .save(
                ExpenseJpaEntity(
                    item.id,
                    item.userId,
                    item.categoryId,
                    item.accountId,
                    item.creditCardId,
                    item.subscriptionId,
                    item.description,
                    item.merchant,
                    item.purchaseDate,
                    item.totalAmount,
                    item.paymentMethod,
                    item.status,
                    item.createdAt,
                ),
            ).toDomain()

    override fun findById(id: UUID) = repository.findById(id).getOrNull()?.toDomain()

    override fun findAllByUserId(userId: UUID) = repository.findAllByUserId(userId).map { it.toDomain() }
}

private fun ExpenseJpaEntity.toDomain() =
    Expense(
        id,
        userId,
        categoryId,
        accountId,
        creditCardId,
        subscriptionId,
        description,
        merchant,
        purchaseDate,
        totalAmount,
        paymentMethod,
        status,
        createdAt,
    )
