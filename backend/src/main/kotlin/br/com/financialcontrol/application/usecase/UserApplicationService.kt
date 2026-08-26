package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CreateUserCommand
import br.com.financialcontrol.application.port.input.CreateUserUseCase
import br.com.financialcontrol.application.port.input.GetUserUseCase
import br.com.financialcontrol.application.port.output.UserPersistencePort
import br.com.financialcontrol.domain.model.User
import java.time.LocalDateTime
import java.util.UUID

class UserApplicationService(
    private val users: UserPersistencePort,
) : CreateUserUseCase,
    GetUserUseCase {
    override fun execute(command: CreateUserCommand): User {
        require(command.name.isNotBlank() && command.email.isNotBlank()) { "name and email are required" }
        val email = command.email.trim().lowercase()
        require(!users.existsByEmail(email)) { "email already exists" }
        return users.save(User(UUID.randomUUID(), command.name.trim(), email, LocalDateTime.now()))
    }

    override fun execute(userId: UUID): User = users.findById(userId) ?: throw NoSuchElementException("User not found")
}
