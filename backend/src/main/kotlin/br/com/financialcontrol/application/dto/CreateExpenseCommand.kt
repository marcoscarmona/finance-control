package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.enum.PaymentMethod
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateExpenseCommand(
    val categoryId: UUID,
    val accountId: UUID?,
    val creditCardId: UUID?,
    val subscriptionId: UUID?,
    val description: String,
    val merchant: String?,
    val purchaseDate: LocalDate,
    val totalAmount: BigDecimal,
    val paymentMethod: PaymentMethod,
    val installments: Int,
)
