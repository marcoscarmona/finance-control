package br.com.financialcontrol.domain.model

import br.com.financialcontrol.domain.enum.ExpenseStatus
import br.com.financialcontrol.domain.enum.PaymentMethod
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Expense(
    val id: UUID,
    val userId: UUID,
    val categoryId: UUID,
    val accountId: UUID?,
    val creditCardId: UUID?,
    val subscriptionId: UUID?,
    val description: String,
    val merchant: String?,
    val purchaseDate: LocalDate,
    val totalAmount: BigDecimal,
    val paymentMethod: PaymentMethod,
    val status: ExpenseStatus = ExpenseStatus.PENDING,
    val createdAt: LocalDateTime,
)
