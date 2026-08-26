package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CreateAccountCommand
import br.com.financialcontrol.application.port.input.CreateAccountUseCase
import br.com.financialcontrol.application.port.input.ListAccountsUseCase
import br.com.financialcontrol.application.port.output.AccountPersistencePort
import br.com.financialcontrol.application.port.output.BankPersistencePort
import br.com.financialcontrol.domain.model.Account
import java.util.UUID

class AccountApplicationService(
    private val banks: BankPersistencePort,
    private val accounts: AccountPersistencePort,
) : CreateAccountUseCase,
    ListAccountsUseCase {
    override fun execute(
        userId: UUID,
        command: CreateAccountCommand,
    ): Account {
        requireOwned(banks.findById(command.bankId)?.userId, userId, "Bank")
        return accounts.save(Account(UUID.randomUUID(), userId, command.bankId, command.name, command.type))
    }

    override fun execute(userId: UUID): List<Account> = accounts.findAllByUserId(userId)
}
