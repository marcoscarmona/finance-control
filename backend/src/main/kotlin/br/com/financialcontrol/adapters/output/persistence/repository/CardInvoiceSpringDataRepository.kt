package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.CardInvoiceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CardInvoiceSpringDataRepository : JpaRepository<CardInvoiceJpaEntity, UUID> {
    fun findByCreditCardIdAndReferenceMonth(
        cardId: UUID,
        month: String,
    ): CardInvoiceJpaEntity?
}
