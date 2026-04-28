package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.enums.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

// User DTOs
data class CreateUserRequest(val name: String, val email: String)
data class UserResponse(val id: UUID, val name: String, val email: String, val createdAt: LocalDateTime)

// Bank DTOs
data class CreateBankRequest(val name: String, val code: String?, val type: BankType)
data class BankResponse(val id: UUID, val userId: UUID, val name: String, val code: String?, val type: BankType, val createdAt: LocalDateTime)

// Account DTOs
data class CreateAccountRequest(val bankId: UUID, val name: String, val type: AccountType)
data class AccountResponse(val id: UUID, val userId: UUID, val bankId: UUID, val name: String, val type: AccountType, val createdAt: LocalDateTime)

// CreditCard DTOs
data class CreateCreditCardRequest(
    val bankId: UUID, val name: String, val lastFourDigits: String,
    val limitAmount: BigDecimal, val closingDay: Int, val dueDay: Int
)
data class CreditCardResponse(
    val id: UUID, val userId: UUID, val bankId: UUID, val name: String,
    val lastFourDigits: String, val limitAmount: BigDecimal,
    val closingDay: Int, val dueDay: Int, val active: Boolean
)

// Category DTOs
data class CreateCategoryRequest(val name: String, val type: CategoryType, val color: String, val icon: String)
data class CategoryResponse(
    val id: UUID, val userId: UUID?, val name: String,
    val type: CategoryType, val color: String, val icon: String, val active: Boolean
)

// Transaction DTOs
data class CreateTransactionRequest(
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
    val installmentNumber: Int?,
    val installmentTotal: Int?,
    val recurring: Boolean = false
)

data class UpdateTransactionRequest(
    val categoryId: UUID?,
    val description: String?,
    val merchant: String?,
    val amount: BigDecimal?,
    val date: LocalDate?
)

data class TransactionResponse(
    val id: UUID,
    val userId: UUID,
    val bankId: UUID?,
    val accountId: UUID?,
    val creditCardId: UUID?,
    val categoryId: UUID,
    val categoryName: String?,
    val categoryColor: String?,
    val bankName: String?,
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

// Budget DTOs
data class CreateBudgetRequest(val categoryId: UUID, val yearMonth: String, val limitAmount: BigDecimal)
data class BudgetResponse(
    val id: UUID, val userId: UUID, val categoryId: UUID,
    val categoryName: String?, val yearMonth: String, val limitAmount: BigDecimal,
    val spent: BigDecimal = BigDecimal.ZERO
)

// Dashboard DTOs
data class ExpenseByCategory(val categoryId: UUID, val categoryName: String, val color: String, val total: BigDecimal)
data class ExpenseByBank(val bankId: UUID?, val bankName: String, val total: BigDecimal)
data class ExpenseByPaymentMethod(val paymentMethod: PaymentMethod, val total: BigDecimal)
data class DailyExpense(val date: LocalDate, val total: BigDecimal)
data class TopMerchant(val merchant: String, val total: BigDecimal, val count: Int)
data class BudgetComparison(
    val categoryId: UUID, val categoryName: String, val color: String,
    val limitAmount: BigDecimal, val spent: BigDecimal, val percentage: Double
)

data class DashboardResponse(
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val balance: BigDecimal,
    val expensesByCategory: List<ExpenseByCategory>,
    val expensesByBank: List<ExpenseByBank>,
    val expensesByPaymentMethod: List<ExpenseByPaymentMethod>,
    val dailyExpenses: List<DailyExpense>,
    val topMerchants: List<TopMerchant>,
    val budgetComparison: List<BudgetComparison>,
    val recentTransactions: List<TransactionResponse>
)
