package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.CreditCardJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.CreditCardSpringDataRepository
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.domain.model.CreditCard
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class CreditCardPersistenceAdapter(
    private val repository: CreditCardSpringDataRepository,
) : CreditCardPersistencePort {
    override fun save(card: CreditCard) =
        repository
            .save(
                CreditCardJpaEntity(
                    card.id,
                    card.userId,
                    card.bankId,
                    card.name,
                    card.lastFourDigits,
                    card.limitAmount,
                    card.closingDay,
                    card.dueDay,
                    card.active,
                ),
            ).toDomain()

    override fun findById(id: UUID) = repository.findById(id).getOrNull()?.toDomain()

    override fun findAllByUserId(userId: UUID) = repository.findAllByUserId(userId).map { it.toDomain() }
}

private fun CreditCardJpaEntity.toDomain() = CreditCard(id, userId, bankId, name, lastFourDigits, limitAmount, closingDay, dueDay, active)
