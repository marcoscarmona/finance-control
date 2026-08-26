package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.ExpenseJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ExpenseSpringDataRepository : JpaRepository<ExpenseJpaEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<ExpenseJpaEntity>
}
