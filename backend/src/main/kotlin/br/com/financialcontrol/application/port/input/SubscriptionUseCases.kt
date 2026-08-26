package br.com.financialcontrol.application.port.input

import br.com.financialcontrol.application.dto.CreateSubscriptionCommand
import br.com.financialcontrol.domain.model.Subscription
import java.util.UUID

interface CreateSubscriptionUseCase {
    fun execute(
        userId: UUID,
        command: CreateSubscriptionCommand,
    ): Subscription
}

interface ListSubscriptionsUseCase {
    fun execute(userId: UUID): List<Subscription>
}
