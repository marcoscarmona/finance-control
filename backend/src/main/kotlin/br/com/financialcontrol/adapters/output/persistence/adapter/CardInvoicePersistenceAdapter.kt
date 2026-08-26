package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.CardInvoiceJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.CardInvoiceSpringDataRepository
import br.com.financialcontrol.application.port.output.CardInvoicePersistencePort
import br.com.financialcontrol.domain.model.CardInvoice
import org.springframework.stereotype.Component
import java.time.YearMonth
import java.util.UUID

@Component
class CardInvoicePersistenceAdapter(
    private val repository: CardInvoiceSpringDataRepository,
) : CardInvoicePersistencePort {
    override fun save(invoice: CardInvoice) =
        repository
            .save(
                CardInvoiceJpaEntity(
                    invoice.id,
                    invoice.creditCardId,
                    invoice.referenceMonth.toString(),
                    invoice.closingDate,
                    invoice.dueDate,
                    invoice.status,
                ),
            ).toDomain()

    override fun findByCardIdAndReferenceMonth(
        cardId: UUID,
        month: YearMonth,
    ) = repository.findByCreditCardIdAndReferenceMonth(cardId, month.toString())?.toDomain()
}

private fun CardInvoiceJpaEntity.toDomain() = CardInvoice(id, creditCardId, YearMonth.parse(referenceMonth), closingDate, dueDate, status)
