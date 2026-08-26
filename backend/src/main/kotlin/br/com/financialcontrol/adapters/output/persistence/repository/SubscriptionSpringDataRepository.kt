package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.SubscriptionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SubscriptionSpringDataRepository : JpaRepository<SubscriptionJpaEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<SubscriptionJpaEntity>
}
