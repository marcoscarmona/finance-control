package br.com.financialcontrol.domain.entities

import br.com.financialcontrol.domain.enums.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class SystemUser(
    val id: UUID,
    val name: String,
    val email: String,
    val createdAt: LocalDateTime
)

data class Bank(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val code: String?,
    val type: BankType,
    val createdAt: LocalDateTime
)

data class Account(
    val id: UUID,
    val userId: UUID,
    val bankId: UUID,
    val name: String,
    val type: AccountType,
    val createdAt: LocalDateTime
)

data class CreditCard(
    val id: UUID,
    val userId: UUID,
    val bankId: UUID,
    val name: String,
    val lastFourDigits: String,
    val limitAmount: BigDecimal,
    val closingDay: Int,
    val dueDay: Int,
    val active: Boolean
)

data class Category(
    val id: UUID,
    val userId: UUID?,
    val name: String,
    val type: CategoryType,
    val color: String,
    val icon: String,
    val active: Boolean
)

data class Transaction(
    val id: UUID,
    val userId: UUID,
    val bankId: UUID?,
    val accountId: UUID?,
    val creditCardId: UUID?,
    val categoryId: UUID,
    val date: LocalDate,
    val description: String,
    val merchant: String?,
    val amount: BigDecimal,
    val type: TransactionType,
    val paymentMethod: PaymentMethod,
    val source: TransactionSource,
    val installmentNumber: Int?,
    val installmentTotal: Int?,
    val recurring: Boolean,
    val createdAt: LocalDateTime
)

data class MonthlyBudget(
    val id: UUID,
    val userId: UUID,
    val categoryId: UUID,
    val yearMonth: String,
    val limitAmount: BigDecimal
)

data class MerchantRule(
    val id: UUID,
    val userId: UUID,
    val pattern: String,
    val categoryId: UUID,
    val active: Boolean
)

data class ImportFile(
    val id: UUID,
    val userId: UUID,
    val fileName: String,
    val type: ImportFileType,
    val importedAt: LocalDateTime,
    val totalTransactions: Int
)
