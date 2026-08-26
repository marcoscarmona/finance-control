package br.com.financialcontrol.domain.model

import br.com.financialcontrol.domain.enum.SubscriptionFrequency
import java.math.BigDecimal
import java.util.UUID

data class Subscription(
    val id: UUID,
    val userId: UUID,
    val categoryId: UUID,
    val creditCardId: UUID?,
    val accountId: UUID?,
    val name: String,
    val amount: BigDecimal,
    val frequency: SubscriptionFrequency,
    val chargeDay: Int,
    val active: Boolean = true,
)
