package br.com.financialcontrol.application.port.output

import br.com.financialcontrol.domain.model.Bank
import java.util.UUID

interface BankPersistencePort {
    fun save(bank: Bank): Bank

    fun findById(id: UUID): Bank?

    fun findAllByUserId(userId: UUID): List<Bank>
}
