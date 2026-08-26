package br.com.financialcontrol.application.port.output

import br.com.financialcontrol.domain.model.Subscription
import java.util.UUID

interface SubscriptionPersistencePort {
    fun save(subscription: Subscription): Subscription

    fun findAllByUserId(userId: UUID): List<Subscription>
}
