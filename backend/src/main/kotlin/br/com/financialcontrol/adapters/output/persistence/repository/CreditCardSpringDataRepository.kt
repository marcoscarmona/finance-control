package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.CreditCardJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CreditCardSpringDataRepository : JpaRepository<CreditCardJpaEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<CreditCardJpaEntity>
}
