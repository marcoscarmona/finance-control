package br.com.financialcontrol.adapters.input.web.request

import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal

data class SetCurrentCardInvoiceTotalRequest(
    @field:DecimalMin("0.00") val totalAmount: BigDecimal,
)
