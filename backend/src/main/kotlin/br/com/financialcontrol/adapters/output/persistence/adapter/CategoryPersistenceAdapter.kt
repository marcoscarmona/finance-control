package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.CategoryJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.CategorySpringDataRepository
import br.com.financialcontrol.application.port.output.CategoryPersistencePort
import br.com.financialcontrol.domain.model.Category
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class CategoryPersistenceAdapter(
    private val repository: CategorySpringDataRepository,
) : CategoryPersistencePort {
    override fun save(category: Category) =
        repository
            .save(CategoryJpaEntity(category.id, category.userId, category.name, category.color, category.active))
            .toDomain()

    override fun findById(id: UUID) = repository.findById(id).getOrNull()?.toDomain()

    override fun findAllByUserId(userId: UUID) = repository.findAllByUserId(userId).map { it.toDomain() }
}

private fun CategoryJpaEntity.toDomain() = Category(id, userId, name, color, active)
