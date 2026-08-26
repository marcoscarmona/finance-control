package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.enum.SubscriptionFrequency
import java.math.BigDecimal
import java.util.UUID

data class CreateSubscriptionCommand(
    val categoryId: UUID,
    val creditCardId: UUID?,
    val accountId: UUID?,
    val name: String,
    val amount: BigDecimal,
    val frequency: SubscriptionFrequency,
    val chargeDay: Int,
)
