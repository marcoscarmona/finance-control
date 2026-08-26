package br.com.financialcontrol.application.port.output

import br.com.financialcontrol.domain.model.CreditCard
import java.util.UUID

interface CreditCardPersistencePort {
    fun save(card: CreditCard): CreditCard

    fun findById(id: UUID): CreditCard?

    fun findAllByUserId(userId: UUID): List<CreditCard>
}
