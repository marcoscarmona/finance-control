package br.com.financialcontrol.domain.model

import br.com.financialcontrol.domain.enum.CardDateRule
import java.math.BigDecimal
import java.util.UUID

data class CreditCard(
    val id: UUID,
    val userId: UUID,
    val bankId: UUID,
    val name: String,
    val lastFourDigits: String,
    val limitAmount: BigDecimal,
    val closingDay: Int,
    val dueDay: Int,
    val closingRule: CardDateRule = CardDateRule.FIXED_DAY,
    val dueRule: CardDateRule = CardDateRule.FIXED_DAY,
    val active: Boolean = true,
)
