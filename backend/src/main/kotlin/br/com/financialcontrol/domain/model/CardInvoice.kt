package br.com.financialcontrol.domain.model

import br.com.financialcontrol.domain.enum.InvoiceStatus
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

data class CardInvoice(
    val id: UUID,
    val creditCardId: UUID,
    val referenceMonth: YearMonth,
    val closingDate: LocalDate,
    val dueDate: LocalDate,
    val status: InvoiceStatus = InvoiceStatus.OPEN,
)
