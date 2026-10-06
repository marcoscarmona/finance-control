package br.com.financialcontrol.application.port.output

import br.com.financialcontrol.domain.model.CardInvoiceManualTotal
import java.time.YearMonth
import java.util.UUID

interface CardInvoiceManualTotalPersistencePort {
    fun save(total: CardInvoiceManualTotal): CardInvoiceManualTotal

    fun findByCardIdAndReferenceMonth(
        cardId: UUID,
        month: YearMonth,
    ): CardInvoiceManualTotal?
}
