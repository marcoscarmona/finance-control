package br.com.financialcontrol.application.port.output

import br.com.financialcontrol.domain.model.Category
import java.util.UUID

interface CategoryPersistencePort {
    fun save(category: Category): Category

    fun findById(id: UUID): Category?

    fun findAllByUserId(userId: UUID): List<Category>
}
