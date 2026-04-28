package br.com.financialcontrol.infrastructure.persistence.entities

import br.com.financialcontrol.domain.enums.*
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "system_users")
class SystemUserJpa(
    @Id @Column(name = "id") var id: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false) var name: String = "",
    @Column(name = "email", nullable = false, unique = true) var email: String = "",
    @Column(name = "created_at") var createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "banks")
class BankJpa(
    @Id @Column(name = "id") var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false) var userId: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false) var name: String = "",
    @Column(name = "code") var code: String? = null,
    @Enumerated(EnumType.STRING) @Column(name = "type", nullable = false) var type: BankType = BankType.BANK,
    @Column(name = "created_at") var createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "accounts")
class AccountJpa(
    @Id @Column(name = "id") var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false) var userId: UUID = UUID.randomUUID(),
    @Column(name = "bank_id", nullable = false) var bankId: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false) var name: String = "",
    @Enumerated(EnumType.STRING) @Column(name = "type", nullable = false) var type: AccountType = AccountType.CHECKING,
    @Column(name = "created_at") var createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "credit_cards")
class CreditCardJpa(
    @Id @Column(name = "id") var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false) var userId: UUID = UUID.randomUUID(),
    @Column(name = "bank_id", nullable = false) var bankId: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false) var name: String = "",
    @Column(name = "last_four_digits", nullable = false) var lastFourDigits: String = "",
    @Column(name = "limit_amount", nullable = false) var limitAmount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "closing_day", nullable = false) var closingDay: Int = 1,
    @Column(name = "due_day", nullable = false) var dueDay: Int = 10,
    @Column(name = "active", nullable = false) var active: Boolean = true
)

@Entity
@Table(name = "categories")
class CategoryJpa(
    @Id @Column(name = "id") var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id") var userId: UUID? = null,
    @Column(name = "name", nullable = false) var name: String = "",
    @Enumerated(EnumType.STRING) @Column(name = "type", nullable = false) var type: CategoryType = CategoryType.EXPENSE,
    @Column(name = "color", nullable = false) var color: String = "#888888",
    @Column(name = "icon", nullable = false) var icon: String = "tag",
    @Column(name = "active", nullable = false) var active: Boolean = true
)

@Entity
@Table(name = "transactions")
class TransactionJpa(
    @Id @Column(name = "id") var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false) var userId: UUID = UUID.randomUUID(),
    @Column(name = "bank_id") var bankId: UUID? = null,
    @Column(name = "account_id") var accountId: UUID? = null,
    @Column(name = "credit_card_id") var creditCardId: UUID? = null,
    @Column(name = "category_id", nullable = false) var categoryId: UUID = UUID.randomUUID(),
    @Column(name = "date", nullable = false) var date: LocalDate = LocalDate.now(),
    @Column(name = "description", nullable = false) var description: String = "",
    @Column(name = "merchant") var merchant: String? = null,
    @Column(name = "amount", nullable = false) var amount: BigDecimal = BigDecimal.ZERO,
    @Enumerated(EnumType.STRING) @Column(name = "type", nullable = false) var type: TransactionType = TransactionType.EXPENSE,
    @Enumerated(EnumType.STRING) @Column(name = "payment_method", nullable = false) var paymentMethod: PaymentMethod = PaymentMethod.PIX,
    @Enumerated(EnumType.STRING) @Column(name = "source", nullable = false) var source: TransactionSource = TransactionSource.MANUAL,
    @Column(name = "installment_number") var installmentNumber: Int? = null,
    @Column(name = "installment_total") var installmentTotal: Int? = null,
    @Column(name = "recurring", nullable = false) var recurring: Boolean = false,
    @Column(name = "created_at") var createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "monthly_budgets")
class MonthlyBudgetJpa(
    @Id @Column(name = "id") var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false) var userId: UUID = UUID.randomUUID(),
    @Column(name = "category_id", nullable = false) var categoryId: UUID = UUID.randomUUID(),
    @Column(name = "year_month", nullable = false) var yearMonth: String = "",
    @Column(name = "limit_amount", nullable = false) var limitAmount: BigDecimal = BigDecimal.ZERO
)

@Entity
@Table(name = "merchant_rules")
class MerchantRuleJpa(
    @Id @Column(name = "id") var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false) var userId: UUID = UUID.randomUUID(),
    @Column(name = "pattern", nullable = false) var pattern: String = "",
    @Column(name = "category_id", nullable = false) var categoryId: UUID = UUID.randomUUID(),
    @Column(name = "active", nullable = false) var active: Boolean = true
)

@Entity
@Table(name = "import_files")
class ImportFileJpa(
    @Id @Column(name = "id") var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false) var userId: UUID = UUID.randomUUID(),
    @Column(name = "file_name", nullable = false) var fileName: String = "",
    @Enumerated(EnumType.STRING) @Column(name = "type", nullable = false) var type: ImportFileType = ImportFileType.CSV,
    @Column(name = "imported_at") var importedAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "total_transactions", nullable = false) var totalTransactions: Int = 0
)
