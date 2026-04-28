package br.com.financialcontrol.infrastructure.persistence.mappers

import br.com.financialcontrol.domain.entities.*
import br.com.financialcontrol.infrastructure.persistence.entities.*

object Mappers {
    fun SystemUserJpa.toDomain() = SystemUser(id, name, email, createdAt)
    fun SystemUser.toJpa() = SystemUserJpa(id, name, email, createdAt)

    fun BankJpa.toDomain() = Bank(id, userId, name, code, type, createdAt)
    fun Bank.toJpa() = BankJpa(id, userId, name, code, type, createdAt)

    fun AccountJpa.toDomain() = Account(id, userId, bankId, name, type, createdAt)
    fun Account.toJpa() = AccountJpa(id, userId, bankId, name, type, createdAt)

    fun CreditCardJpa.toDomain() = CreditCard(id, userId, bankId, name, lastFourDigits, limitAmount, closingDay, dueDay, active)
    fun CreditCard.toJpa() = CreditCardJpa(id, userId, bankId, name, lastFourDigits, limitAmount, closingDay, dueDay, active)

    fun CategoryJpa.toDomain() = Category(id, userId, name, type, color, icon, active)
    fun Category.toJpa() = CategoryJpa(id, userId, name, type, color, icon, active)

    fun TransactionJpa.toDomain() = Transaction(
        id, userId, bankId, accountId, creditCardId, categoryId, date,
        description, merchant, amount, type, paymentMethod, source,
        installmentNumber, installmentTotal, recurring, createdAt
    )
    fun Transaction.toJpa() = TransactionJpa(
        id, userId, bankId, accountId, creditCardId, categoryId, date,
        description, merchant, amount, type, paymentMethod, source,
        installmentNumber, installmentTotal, recurring, createdAt
    )

    fun MonthlyBudgetJpa.toDomain() = MonthlyBudget(id, userId, categoryId, yearMonth, limitAmount)
    fun MonthlyBudget.toJpa() = MonthlyBudgetJpa(id, userId, categoryId, yearMonth, limitAmount)

    fun ImportFileJpa.toDomain() = ImportFile(id, userId, fileName, type, importedAt, totalTransactions)
    fun ImportFile.toJpa() = ImportFileJpa(id, userId, fileName, type, importedAt, totalTransactions)
}
