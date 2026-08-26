package br.com.financialcontrol.adapters.input.web.request

import br.com.financialcontrol.domain.enum.SubscriptionFrequency
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.util.UUID

data class CreateSubscriptionRequest(
    val categoryId: UUID,
    val creditCardId: UUID? = null,
    val accountId: UUID? = null,
    @field:NotBlank val name: String,
    @field:DecimalMin("0.01") val amount: BigDecimal,
    val frequency: SubscriptionFrequency,
    @field:Min(1) @field:Max(31) val chargeDay: Int,
)
