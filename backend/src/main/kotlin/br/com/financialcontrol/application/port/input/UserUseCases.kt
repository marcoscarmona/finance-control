package br.com.financialcontrol.application.port.input

import br.com.financialcontrol.application.dto.CreateUserCommand
import br.com.financialcontrol.domain.model.User
import java.util.UUID

interface CreateUserUseCase {
    fun execute(command: CreateUserCommand): User
}

interface GetUserUseCase {
    fun execute(userId: UUID): User
}
