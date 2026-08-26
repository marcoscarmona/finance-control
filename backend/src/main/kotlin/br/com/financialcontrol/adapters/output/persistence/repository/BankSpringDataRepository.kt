package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.BankJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BankSpringDataRepository : JpaRepository<BankJpaEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<BankJpaEntity>
}
