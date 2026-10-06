package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.SetCurrentCardInvoiceTotalCommand
import br.com.financialcontrol.application.port.input.SetCurrentCardInvoiceTotalUseCase
import br.com.financialcontrol.application.port.output.CardInvoiceManualTotalPersistencePort
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.domain.model.CardInvoiceManualTotal
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID

class ManualCardInvoiceTotalApplicationService(
    private val cards: CreditCardPersistencePort,
    private val totals: CardInvoiceManualTotalPersistencePort,
) : SetCurrentCardInvoiceTotalUseCase {
    override fun execute(
        userId: UUID,
        cardId: UUID,
        command: SetCurrentCardInvoiceTotalCommand,
    ): CardInvoiceManualTotal {
        val card = cards.findById(cardId)
        require(card?.userId == userId) { "Credit card not found for user" }
        require(command.totalAmount >= BigDecimal.ZERO) { "totalAmount must be greater than or equal to zero" }

        val currentMonth = YearMonth.now()
        val existing = totals.findByCardIdAndReferenceMonth(cardId, currentMonth)
        return totals.save(
            CardInvoiceManualTotal(
                existing?.id ?: UUID.randomUUID(),
                cardId,
                currentMonth,
                command.totalAmount.setScale(2),
                LocalDateTime.now(),
            ),
        )
    }
}
