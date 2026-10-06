package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.CardInvoiceManualTotalJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CardInvoiceManualTotalSpringDataRepository : JpaRepository<CardInvoiceManualTotalJpaEntity, UUID> {
    fun findByCreditCardIdAndReferenceMonth(
        creditCardId: UUID,
        referenceMonth: String,
    ): CardInvoiceManualTotalJpaEntity?
}
