package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.AccountJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountSpringDataRepository : JpaRepository<AccountJpaEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<AccountJpaEntity>
}
