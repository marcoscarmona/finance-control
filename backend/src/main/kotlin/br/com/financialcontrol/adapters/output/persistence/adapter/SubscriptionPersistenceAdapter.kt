package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.SubscriptionJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.SubscriptionSpringDataRepository
import br.com.financialcontrol.application.port.output.SubscriptionPersistencePort
import br.com.financialcontrol.domain.model.Subscription
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SubscriptionPersistenceAdapter(
    private val repository: SubscriptionSpringDataRepository,
) : SubscriptionPersistencePort {
    override fun save(subscription: Subscription) =
        repository
            .save(
                SubscriptionJpaEntity(
                    subscription.id,
                    subscription.userId,
                    subscription.categoryId,
                    subscription.creditCardId,
                    subscription.accountId,
                    subscription.name,
                    subscription.amount,
                    subscription.frequency,
                    subscription.chargeDay,
                    subscription.active,
                ),
            ).toDomain()

    override fun findAllByUserId(userId: UUID) = repository.findAllByUserId(userId).map { it.toDomain() }
}

private fun SubscriptionJpaEntity.toDomain() =
    Subscription(id, userId, categoryId, creditCardId, accountId, name, amount, frequency, chargeDay, active)
