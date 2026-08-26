package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.CategoryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CategorySpringDataRepository : JpaRepository<CategoryJpaEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<CategoryJpaEntity>
}
