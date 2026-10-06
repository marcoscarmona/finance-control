package br.com.financialcontrol.application.port.input

import br.com.financialcontrol.application.dto.SetCurrentCardInvoiceTotalCommand
import br.com.financialcontrol.domain.model.CardInvoiceManualTotal
import java.util.UUID

interface SetCurrentCardInvoiceTotalUseCase {
    fun execute(
        userId: UUID,
        cardId: UUID,
        command: SetCurrentCardInvoiceTotalCommand,
    ): CardInvoiceManualTotal
}
