package br.com.financialcontrol.application.ports.out

import br.com.financialcontrol.domain.entities.*
import br.com.financialcontrol.domain.enums.TransactionType
import java.time.LocalDate
import java.util.UUID

interface UserRepository {
    fun save(user: SystemUser): SystemUser
    fun findById(id: UUID): SystemUser?
    fun findByEmail(email: String): SystemUser?
    fun findDemo(): SystemUser?
}

interface BankRepository {
    fun save(bank: Bank): Bank
    fun findAllByUserId(userId: UUID): List<Bank>
    fun findById(id: UUID): Bank?
}

interface AccountRepository {
    fun save(account: Account): Account
    fun findAllByUserId(userId: UUID): List<Account>
}

interface CreditCardRepository {
    fun save(card: CreditCard): CreditCard
    fun findAllByUserId(userId: UUID): List<CreditCard>
}

interface CategoryRepository {
    fun save(category: Category): Category
    fun findAllByUserId(userId: UUID): List<Category>
    fun findById(id: UUID): Category?
}

interface TransactionRepository {
    fun save(transaction: Transaction): Transaction
    fun update(transaction: Transaction): Transaction
    fun delete(id: UUID)
    fun findById(id: UUID): Transaction?
    fun findAllByUserIdAndYearMonth(userId: UUID, yearMonth: String, bankId: UUID?, categoryId: UUID?): List<Transaction>
    fun findRecentByUserId(userId: UUID, limit: Int): List<Transaction>
    fun findByUserIdAndDateBetween(userId: UUID, startDate: LocalDate, endDate: LocalDate): List<Transaction>
}

interface BudgetRepository {
    fun save(budget: MonthlyBudget): MonthlyBudget
    fun findAllByUserIdAndYearMonth(userId: UUID, yearMonth: String): List<MonthlyBudget>
    fun findById(id: UUID): MonthlyBudget?
}

interface ImportFileRepository {
    fun save(importFile: ImportFile): ImportFile
}
