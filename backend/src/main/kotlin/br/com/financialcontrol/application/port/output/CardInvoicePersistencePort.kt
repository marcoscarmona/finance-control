package br.com.financialcontrol.application.port.output

import br.com.financialcontrol.domain.model.CardInvoice
import java.time.YearMonth
import java.util.UUID

interface CardInvoicePersistencePort {
    fun save(invoice: CardInvoice): CardInvoice

    fun findByCardIdAndReferenceMonth(
        cardId: UUID,
        month: YearMonth,
    ): CardInvoice?
}
