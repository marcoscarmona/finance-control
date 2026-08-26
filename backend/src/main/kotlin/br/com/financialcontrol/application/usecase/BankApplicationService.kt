package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CreateBankCommand
import br.com.financialcontrol.application.port.input.CreateBankUseCase
import br.com.financialcontrol.application.port.input.ListBanksUseCase
import br.com.financialcontrol.application.port.output.BankPersistencePort
import br.com.financialcontrol.application.port.output.UserPersistencePort
import br.com.financialcontrol.domain.model.Bank
import java.util.UUID

class BankApplicationService(
    private val users: UserPersistencePort,
    private val banks: BankPersistencePort,
) : CreateBankUseCase,
    ListBanksUseCase {
    override fun execute(
        userId: UUID,
        command: CreateBankCommand,
    ): Bank {
        requireUser(users, userId)
        return banks.save(Bank(UUID.randomUUID(), userId, command.name, command.code, command.type))
    }

    override fun execute(userId: UUID): List<Bank> = banks.findAllByUserId(userId)
}
