package br.com.financialcontrol.application.port.output

import br.com.financialcontrol.domain.model.Account
import java.util.UUID

interface AccountPersistencePort {
    fun save(account: Account): Account

    fun findById(id: UUID): Account?

    fun findAllByUserId(userId: UUID): List<Account>
}
