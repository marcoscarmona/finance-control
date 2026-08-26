package br.com.financialcontrol.domain

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID

enum class BankType { BANK, DIGITAL_WALLET }
enum class AccountType { CHECKING, SAVINGS, CASH }
enum class PaymentMethod { CREDIT_CARD, DEBIT_CARD, PIX, BOLETO, CASH, BANK_TRANSFER }
enum class ExpenseStatus { PENDING, PAID, CANCELED }
enum class InvoiceStatus { OPEN, CLOSED, PAID }
enum class SubscriptionFrequency { MONTHLY, QUARTERLY, YEARLY }

data class User(val id: UUID, val name: String, val email: String, val createdAt: LocalDateTime)
data class Bank(val id: UUID, val userId: UUID, val name: String, val code: String?, val type: BankType)
data class Account(val id: UUID, val userId: UUID, val bankId: UUID, val name: String, val type: AccountType)
data class Category(val id: UUID, val userId: UUID, val name: String, val color: String, val active: Boolean = true)
data class CreditCard(val id: UUID, val userId: UUID, val bankId: UUID, val name: String, val lastFourDigits: String, val limitAmount: BigDecimal, val closingDay: Int, val dueDay: Int, val active: Boolean = true)
data class CardInvoice(val id: UUID, val creditCardId: UUID, val referenceMonth: YearMonth, val closingDate: LocalDate, val dueDate: LocalDate, val status: InvoiceStatus = InvoiceStatus.OPEN)
data class Expense(val id: UUID, val userId: UUID, val categoryId: UUID, val accountId: UUID?, val creditCardId: UUID?, val subscriptionId: UUID?, val description: String, val merchant: String?, val purchaseDate: LocalDate, val totalAmount: BigDecimal, val paymentMethod: PaymentMethod, val status: ExpenseStatus = ExpenseStatus.PENDING, val createdAt: LocalDateTime)
data class ExpenseInstallment(val id: UUID, val expenseId: UUID, val invoiceId: UUID?, val number: Int, val total: Int, val amount: BigDecimal, val dueDate: LocalDate, val status: ExpenseStatus = ExpenseStatus.PENDING)
data class Subscription(val id: UUID, val userId: UUID, val categoryId: UUID, val creditCardId: UUID?, val accountId: UUID?, val name: String, val amount: BigDecimal, val frequency: SubscriptionFrequency, val chargeDay: Int, val active: Boolean = true)
