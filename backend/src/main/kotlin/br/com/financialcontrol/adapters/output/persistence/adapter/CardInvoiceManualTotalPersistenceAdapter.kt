package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.CardInvoiceManualTotalJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.CardInvoiceManualTotalSpringDataRepository
import br.com.financialcontrol.application.port.output.CardInvoiceManualTotalPersistencePort
import br.com.financialcontrol.domain.model.CardInvoiceManualTotal
import org.springframework.stereotype.Component
import java.time.YearMonth
import java.util.UUID

@Component
class CardInvoiceManualTotalPersistenceAdapter(
    private val repository: CardInvoiceManualTotalSpringDataRepository,
) : CardInvoiceManualTotalPersistencePort {
    override fun save(total: CardInvoiceManualTotal): CardInvoiceManualTotal = repository.save(total.toEntity()).toDomain()

    override fun findByCardIdAndReferenceMonth(
        cardId: UUID,
        month: YearMonth,
    ): CardInvoiceManualTotal? = repository.findByCreditCardIdAndReferenceMonth(cardId, month.toString())?.toDomain()
}

private fun CardInvoiceManualTotal.toEntity() =
    CardInvoiceManualTotalJpaEntity(id, creditCardId, referenceMonth.toString(), totalAmount, updatedAt)

private fun CardInvoiceManualTotalJpaEntity.toDomain() =
    CardInvoiceManualTotal(id, creditCardId, YearMonth.parse(referenceMonth), totalAmount, updatedAt)
