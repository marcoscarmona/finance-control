package br.com.financialcontrol.infrastructure.adapters

import br.com.financialcontrol.application.ports.out.*
import br.com.financialcontrol.domain.entities.*
import br.com.financialcontrol.infrastructure.persistence.mappers.Mappers.toDomain
import br.com.financialcontrol.infrastructure.persistence.mappers.Mappers.toJpa
import br.com.financialcontrol.infrastructure.persistence.repositories.*
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class UserRepositoryAdapter(private val jpa: SystemUserJpaRepository) : UserRepository {
    override fun save(user: SystemUser) = jpa.save(user.toJpa()).toDomain()
    override fun findById(id: UUID) = jpa.findById(id).getOrNull()?.toDomain()
    override fun findByEmail(email: String) = jpa.findByEmail(email)?.toDomain()
    override fun findDemo() = jpa.findDemo()?.toDomain()
}

@Component
class BankRepositoryAdapter(private val jpa: BankJpaRepository) : BankRepository {
    override fun save(bank: Bank) = jpa.save(bank.toJpa()).toDomain()
    override fun findAllByUserId(userId: UUID) = jpa.findAllByUserId(userId).map { it.toDomain() }
    override fun findById(id: UUID) = jpa.findById(id).getOrNull()?.toDomain()
}

@Component
class AccountRepositoryAdapter(private val jpa: AccountJpaRepository) : AccountRepository {
    override fun save(account: Account) = jpa.save(account.toJpa()).toDomain()
    override fun findAllByUserId(userId: UUID) = jpa.findAllByUserId(userId).map { it.toDomain() }
}

@Component
class CreditCardRepositoryAdapter(private val jpa: CreditCardJpaRepository) : CreditCardRepository {
    override fun save(card: CreditCard) = jpa.save(card.toJpa()).toDomain()
    override fun findAllByUserId(userId: UUID) = jpa.findAllByUserId(userId).map { it.toDomain() }
}

@Component
class CategoryRepositoryAdapter(private val jpa: CategoryJpaRepository) : CategoryRepository {
    override fun save(category: Category) = jpa.save(category.toJpa()).toDomain()
    override fun findAllByUserId(userId: UUID) = jpa.findAllByUserIdOrDefault(userId).map { it.toDomain() }
    override fun findById(id: UUID) = jpa.findById(id).getOrNull()?.toDomain()
}

@Component
class TransactionRepositoryAdapter(private val jpa: TransactionJpaRepository) : TransactionRepository {
    override fun save(transaction: Transaction) = jpa.save(transaction.toJpa()).toDomain()
    override fun update(transaction: Transaction) = jpa.save(transaction.toJpa()).toDomain()
    override fun delete(id: UUID) = jpa.deleteById(id)
    override fun findById(id: UUID) = jpa.findById(id).getOrNull()?.toDomain()
    override fun findAllByUserIdAndYearMonth(userId: UUID, yearMonth: String, bankId: UUID?, categoryId: UUID?) =
        jpa.findByUserIdAndYearMonth(userId, yearMonth, bankId, categoryId).map { it.toDomain() }
    override fun findRecentByUserId(userId: UUID, limit: Int) =
        jpa.findRecentByUserId(userId, PageRequest.of(0, limit)).map { it.toDomain() }
    override fun findByUserIdAndDateBetween(userId: UUID, startDate: LocalDate, endDate: LocalDate) =
        jpa.findByUserIdAndDateBetween(userId, startDate, endDate).map { it.toDomain() }
}

@Component
class BudgetRepositoryAdapter(private val jpa: MonthlyBudgetJpaRepository) : BudgetRepository {
    override fun save(budget: MonthlyBudget) = jpa.save(budget.toJpa()).toDomain()
    override fun findAllByUserIdAndYearMonth(userId: UUID, yearMonth: String) =
        jpa.findAllByUserIdAndYearMonth(userId, yearMonth).map { it.toDomain() }
    override fun findById(id: UUID) = jpa.findById(id).getOrNull()?.toDomain()
}

@Component
class ImportFileRepositoryAdapter(private val jpa: ImportFileJpaRepository) : ImportFileRepository {
    override fun save(importFile: ImportFile) = jpa.save(importFile.toJpa()).toDomain()
}
