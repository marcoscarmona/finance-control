package br.com.financialcontrol.application

import br.com.financialcontrol.domain.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID

interface FinanceRepository {
    fun saveUser(user: User): User; fun user(id: UUID): User?; fun emailExists(email: String): Boolean
    fun saveBank(bank: Bank): Bank; fun banks(userId: UUID): List<Bank>; fun bank(id: UUID): Bank?
    fun saveAccount(account: Account): Account; fun accounts(userId: UUID): List<Account>; fun account(id: UUID): Account?
    fun saveCategory(category: Category): Category; fun categories(userId: UUID): List<Category>; fun category(id: UUID): Category?
    fun saveCard(card: CreditCard): CreditCard; fun cards(userId: UUID): List<CreditCard>; fun card(id: UUID): CreditCard?
    fun saveSubscription(subscription: Subscription): Subscription; fun subscriptions(userId: UUID): List<Subscription>
    fun saveExpense(expense: Expense): Expense; fun expense(id: UUID): Expense?; fun expenses(userId: UUID): List<Expense>
    fun saveInstallments(items: List<ExpenseInstallment>); fun installmentsForExpense(expenseId: UUID): List<ExpenseInstallment>; fun installmentsBetween(userId: UUID, start: LocalDate, end: LocalDate): List<ExpenseInstallment>
    fun invoice(cardId: UUID, month: YearMonth): CardInvoice?; fun saveInvoice(invoice: CardInvoice): CardInvoice
}

data class CreateExpenseCommand(val categoryId: UUID, val accountId: UUID?, val creditCardId: UUID?, val subscriptionId: UUID?, val description: String, val merchant: String?, val purchaseDate: LocalDate, val totalAmount: BigDecimal, val paymentMethod: PaymentMethod, val installments: Int)

class InstallmentCalculator {
    fun split(total: BigDecimal, count: Int): List<BigDecimal> {
        require(count > 0) { "installments must be positive" }; require(total > BigDecimal.ZERO) { "totalAmount must be positive" }
        val base = total.divide(BigDecimal(count), 2, RoundingMode.DOWN)
        val remainder = total.subtract(base.multiply(BigDecimal(count)))
        return List(count) { index -> if (index == count - 1) base.add(remainder) else base }
    }
    fun referenceMonth(date: LocalDate, closingDay: Int): YearMonth = if (date.dayOfMonth >= closingDay) YearMonth.from(date).plusMonths(1) else YearMonth.from(date)
}

class FinanceService(private val repository: FinanceRepository, private val calculator: InstallmentCalculator = InstallmentCalculator()) {
    fun createUser(name: String, email: String): User {
        require(name.isNotBlank() && email.isNotBlank()) { "name and email are required" }; require(!repository.emailExists(email)) { "email already exists" }
        return repository.saveUser(User(UUID.randomUUID(), name.trim(), email.trim().lowercase(), LocalDateTime.now()))
    }
    fun user(id: UUID) = repository.user(id) ?: throw NoSuchElementException("User not found")
    fun createBank(userId: UUID, name: String, code: String?, type: BankType): Bank { user(userId); return repository.saveBank(Bank(UUID.randomUUID(), userId, name, code, type)) }
    fun createAccount(userId: UUID, bankId: UUID, name: String, type: AccountType): Account { assertOwned(repository.bank(bankId)?.userId, userId, "Bank"); return repository.saveAccount(Account(UUID.randomUUID(), userId, bankId, name, type)) }
    fun createCategory(userId: UUID, name: String, color: String): Category { user(userId); return repository.saveCategory(Category(UUID.randomUUID(), userId, name, color)) }
    fun createCard(userId: UUID, bankId: UUID, name: String, lastFourDigits: String, limitAmount: BigDecimal, closingDay: Int, dueDay: Int): CreditCard {
        assertOwned(repository.bank(bankId)?.userId, userId, "Bank"); require(closingDay in 1..31 && dueDay in 1..31) { "closingDay and dueDay must be between 1 and 31" }
        return repository.saveCard(CreditCard(UUID.randomUUID(), userId, bankId, name, lastFourDigits, limitAmount, closingDay, dueDay))
    }
    fun createSubscription(userId: UUID, categoryId: UUID, cardId: UUID?, accountId: UUID?, name: String, amount: BigDecimal, frequency: SubscriptionFrequency, chargeDay: Int): Subscription {
        assertOwned(repository.category(categoryId)?.userId, userId, "Category"); cardId?.let { assertOwned(repository.card(it)?.userId, userId, "Credit card") }; accountId?.let { assertOwned(repository.account(it)?.userId, userId, "Account") }
        require(chargeDay in 1..31 && amount > BigDecimal.ZERO) { "invalid subscription amount or charge day" }
        return repository.saveSubscription(Subscription(UUID.randomUUID(), userId, categoryId, cardId, accountId, name, amount, frequency, chargeDay))
    }
    fun createExpense(userId: UUID, command: CreateExpenseCommand): Pair<Expense, List<ExpenseInstallment>> {
        assertOwned(repository.category(command.categoryId)?.userId, userId, "Category"); command.accountId?.let { assertOwned(repository.account(it)?.userId, userId, "Account") }; command.creditCardId?.let { assertOwned(repository.card(it)?.userId, userId, "Credit card") }
        require((command.paymentMethod == PaymentMethod.CREDIT_CARD) == (command.creditCardId != null)) { "creditCardId is required only for CREDIT_CARD" }
        val expense = repository.saveExpense(Expense(UUID.randomUUID(), userId, command.categoryId, command.accountId, command.creditCardId, command.subscriptionId, command.description, command.merchant, command.purchaseDate, command.totalAmount, command.paymentMethod, createdAt = LocalDateTime.now()))
        val values = calculator.split(command.totalAmount, command.installments)
        val rows = values.mapIndexed { index, value ->
            val due = command.purchaseDate.plusMonths(index.toLong())
            val invoice = command.creditCardId?.let { cardId -> invoiceFor(repository.card(cardId)!!, due) }
            ExpenseInstallment(UUID.randomUUID(), expense.id, invoice?.id, index + 1, values.size, value, invoice?.dueDate ?: due)
        }
        repository.saveInstallments(rows); return expense to rows
    }
    fun invoice(userId: UUID, cardId: UUID, month: YearMonth): Pair<CardInvoice, List<ExpenseInstallment>> { assertOwned(repository.card(cardId)?.userId, userId, "Credit card"); val invoice = invoiceFor(repository.card(cardId)!!, month.atDay(1)); return invoice to repository.installmentsBetween(userId, invoice.closingDate.minusMonths(1).plusDays(1), invoice.closingDate).filter { it.invoiceId == invoice.id } }
    fun monthlyReport(userId: UUID, month: YearMonth): MonthlyReport { user(userId); val items = repository.installmentsBetween(userId, month.atDay(1), month.atEndOfMonth()); val expenses = repository.expenses(userId).associateBy { it.id }; val byCategory = items.groupBy { expenses[it.expenseId]?.categoryId }.mapNotNull { (id, rows) -> id?.let { CategoryTotal(repository.category(it)?.name ?: "Unknown", rows.sumOf { row -> row.amount }) } }.sortedByDescending { it.total }; val future = repository.installmentsBetween(userId, month.plusMonths(1).atDay(1), month.plusMonths(3).atEndOfMonth()); return MonthlyReport(items.sumOf { it.amount }, byCategory, repository.cards(userId).map { card -> CardTotal(card.id, card.name, items.filter { expenses[it.expenseId]?.creditCardId == card.id }.sumOf { it.amount }) }, repository.subscriptions(userId).filter { it.active }.map { SubscriptionForecast(it.name, it.amount, it.frequency) }, future) }
    fun banks(userId: UUID) = repository.banks(userId); fun accounts(userId: UUID) = repository.accounts(userId); fun categories(userId: UUID) = repository.categories(userId); fun cards(userId: UUID) = repository.cards(userId); fun subscriptions(userId: UUID) = repository.subscriptions(userId); fun expenses(userId: UUID) = repository.expenses(userId)
    private fun invoiceFor(card: CreditCard, date: LocalDate): CardInvoice { val ref = calculator.referenceMonth(date, card.closingDay); return repository.invoice(card.id, ref) ?: repository.saveInvoice(CardInvoice(UUID.randomUUID(), card.id, ref, ref.atDay(card.closingDay.coerceAtMost(ref.lengthOfMonth())), ref.atDay(card.dueDay.coerceAtMost(ref.lengthOfMonth())).plusMonths(1))) }
    private fun assertOwned(actual: UUID?, expected: UUID, label: String) { require(actual == expected) { "$label not found for user" } }
}
data class CategoryTotal(val category: String, val total: BigDecimal); data class CardTotal(val cardId: UUID, val cardName: String, val total: BigDecimal); data class SubscriptionForecast(val name: String, val amount: BigDecimal, val frequency: SubscriptionFrequency); data class MonthlyReport(val totalExpenses: BigDecimal, val expensesByCategory: List<CategoryTotal>, val cardInvoices: List<CardTotal>, val activeSubscriptions: List<SubscriptionForecast>, val upcomingInstallments: List<ExpenseInstallment>)
