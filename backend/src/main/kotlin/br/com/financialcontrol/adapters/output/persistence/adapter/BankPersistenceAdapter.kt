package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.BankJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.BankSpringDataRepository
import br.com.financialcontrol.application.port.output.BankPersistencePort
import br.com.financialcontrol.domain.model.Bank
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class BankPersistenceAdapter(
    private val repository: BankSpringDataRepository,
) : BankPersistencePort {
    override fun save(bank: Bank) = repository.save(BankJpaEntity(bank.id, bank.userId, bank.name, bank.code, bank.type)).toDomain()

    override fun findById(id: UUID) = repository.findById(id).getOrNull()?.toDomain()

    override fun findAllByUserId(userId: UUID) = repository.findAllByUserId(userId).map { it.toDomain() }
}

private fun BankJpaEntity.toDomain() = Bank(id, userId, name, code, type)
