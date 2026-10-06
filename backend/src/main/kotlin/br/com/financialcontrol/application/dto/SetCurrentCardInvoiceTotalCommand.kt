package br.com.financialcontrol.application.dto

import java.math.BigDecimal

data class SetCurrentCardInvoiceTotalCommand(
    val totalAmount: BigDecimal,
)
