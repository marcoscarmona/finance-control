package br.com.financialcontrol.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID

data class CardInvoiceManualTotal(
    val id: UUID,
    val creditCardId: UUID,
    val referenceMonth: YearMonth,
    val totalAmount: BigDecimal,
    val updatedAt: LocalDateTime,
)
