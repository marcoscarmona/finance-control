package br.com.financialcontrol.adapters.input.web.request

import br.com.financialcontrol.domain.enum.PaymentMethod
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateExpenseRequest(
    val categoryId: UUID,
    val accountId: UUID? = null,
    val creditCardId: UUID? = null,
    val subscriptionId: UUID? = null,
    @field:NotBlank val description: String,
    val merchant: String? = null,
    val purchaseDate: LocalDate,
    @field:DecimalMin("0.01") val totalAmount: BigDecimal,
    val paymentMethod: PaymentMethod,
    @field:Min(1) val installments: Int = 1,
)
