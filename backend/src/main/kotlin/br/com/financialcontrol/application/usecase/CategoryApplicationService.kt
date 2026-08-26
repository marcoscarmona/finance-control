package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CreateCategoryCommand
import br.com.financialcontrol.application.port.input.CreateCategoryUseCase
import br.com.financialcontrol.application.port.input.ListCategoriesUseCase
import br.com.financialcontrol.application.port.output.CategoryPersistencePort
import br.com.financialcontrol.application.port.output.UserPersistencePort
import br.com.financialcontrol.domain.model.Category
import java.util.UUID

class CategoryApplicationService(
    private val users: UserPersistencePort,
    private val categories: CategoryPersistencePort,
) : CreateCategoryUseCase,
    ListCategoriesUseCase {
    override fun execute(
        userId: UUID,
        command: CreateCategoryCommand,
    ): Category {
        requireUser(users, userId)
        return categories.save(Category(UUID.randomUUID(), userId, command.name, command.color))
    }

    override fun execute(userId: UUID): List<Category> = categories.findAllByUserId(userId)
}
