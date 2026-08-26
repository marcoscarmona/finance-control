package br.com.financialcontrol.application.port.input

import br.com.financialcontrol.application.dto.CreateAccountCommand
import br.com.financialcontrol.domain.model.Account
import java.util.UUID

interface CreateAccountUseCase {
    fun execute(
        userId: UUID,
        command: CreateAccountCommand,
    ): Account
}

interface ListAccountsUseCase {
    fun execute(userId: UUID): List<Account>
}
