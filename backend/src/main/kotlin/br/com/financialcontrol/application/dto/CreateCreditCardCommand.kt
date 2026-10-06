package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.enum.CardDateRule
import java.math.BigDecimal
import java.util.UUID

data class CreateCreditCardCommand(
    val bankId: UUID,
    val name: String,
    val lastFourDigits: String,
    val limitAmount: BigDecimal,
    val closingDay: Int,
    val dueDay: Int,
    val closingRule: CardDateRule = CardDateRule.FIXED_DAY,
    val dueRule: CardDateRule = CardDateRule.FIXED_DAY,
)
