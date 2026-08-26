package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.AccountJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.AccountSpringDataRepository
import br.com.financialcontrol.application.port.output.AccountPersistencePort
import br.com.financialcontrol.domain.model.Account
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class AccountPersistenceAdapter(
    private val repository: AccountSpringDataRepository,
) : AccountPersistencePort {
    override fun save(account: Account) =
        repository
            .save(AccountJpaEntity(account.id, account.userId, account.bankId, account.name, account.type))
            .toDomain()

    override fun findById(id: UUID) = repository.findById(id).getOrNull()?.toDomain()

    override fun findAllByUserId(userId: UUID) = repository.findAllByUserId(userId).map { it.toDomain() }
}

private fun AccountJpaEntity.toDomain() = Account(id, userId, bankId, name, type)
