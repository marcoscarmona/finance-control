package br.com.financialcontrol.adapters.input.web.request

import br.com.financialcontrol.domain.enum.CardDateRule
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.math.BigDecimal
import java.util.UUID

data class CreateCreditCardRequest(
    val bankId: UUID,
    @field:NotBlank val name: String,
    @field:Pattern(regexp = "^[0-9]{4}$") val lastFourDigits: String,
    @field:DecimalMin("0.01") val limitAmount: BigDecimal,
    @field:Min(1) @field:Max(31) val closingDay: Int,
    @field:Min(1) @field:Max(31) val dueDay: Int,
    val closingRule: CardDateRule = CardDateRule.FIXED_DAY,
    val dueRule: CardDateRule = CardDateRule.FIXED_DAY,
)
