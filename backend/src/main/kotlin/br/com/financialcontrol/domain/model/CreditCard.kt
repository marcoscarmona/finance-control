package br.com.financialcontrol.domain.model

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
    val active: Boolean = true,
)
