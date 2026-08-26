package br.com.financialcontrol.application.port.input

import br.com.financialcontrol.application.dto.CreateCategoryCommand
import br.com.financialcontrol.domain.model.Category
import java.util.UUID

interface CreateCategoryUseCase {
    fun execute(
        userId: UUID,
        command: CreateCategoryCommand,
    ): Category
}

interface ListCategoriesUseCase {
    fun execute(userId: UUID): List<Category>
}
