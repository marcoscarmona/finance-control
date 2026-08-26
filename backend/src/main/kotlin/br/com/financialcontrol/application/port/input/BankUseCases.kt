package br.com.financialcontrol.application.port.input

import br.com.financialcontrol.application.dto.CreateBankCommand
import br.com.financialcontrol.domain.model.Bank
import java.util.UUID

interface CreateBankUseCase {
    fun execute(
        userId: UUID,
        command: CreateBankCommand,
    ): Bank
}

interface ListBanksUseCase {
    fun execute(userId: UUID): List<Bank>
}
