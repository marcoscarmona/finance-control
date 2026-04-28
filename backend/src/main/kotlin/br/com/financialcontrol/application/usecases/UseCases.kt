package br.com.financialcontrol.application.usecases

import br.com.financialcontrol.application.dto.*
import br.com.financialcontrol.application.ports.out.*
import br.com.financialcontrol.domain.entities.*
import br.com.financialcontrol.domain.enums.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class UserUseCase(private val userRepository: UserRepository) {
    fun createUser(req: CreateUserRequest): UserResponse {
        val user = SystemUser(UUID.randomUUID(), req.name, req.email, LocalDateTime.now())
        val saved = userRepository.save(user)
        return saved.toResponse()
    }

    fun getUser(id: UUID): UserResponse =
        userRepository.findById(id)?.toResponse() ?: throw NoSuchElementException("User not found")

    fun getDemoUser(): UserResponse =
        userRepository.findDemo()?.toResponse() ?: throw NoSuchElementException("Demo user not found")

    private fun SystemUser.toResponse() = UserResponse(id, name, email, createdAt)
}

@Service
class BankUseCase(private val bankRepository: BankRepository) {
    fun createBank(userId: UUID, req: CreateBankRequest): BankResponse {
        val bank = Bank(UUID.randomUUID(), userId, req.name, req.code, req.type, LocalDateTime.now())
        return bankRepository.save(bank).toResponse()
    }

    fun getBanks(userId: UUID): List<BankResponse> = bankRepository.findAllByUserId(userId).map { it.toResponse() }

    private fun Bank.toResponse() = BankResponse(id, userId, name, code, type, createdAt)
}

@Service
class AccountUseCase(private val accountRepository: AccountRepository) {
    fun createAccount(userId: UUID, req: CreateAccountRequest): AccountResponse {
        val account = Account(UUID.randomUUID(), userId, req.bankId, req.name, req.type, LocalDateTime.now())
        return accountRepository.save(account).toResponse()
    }

    fun getAccounts(userId: UUID): List<AccountResponse> = accountRepository.findAllByUserId(userId).map { it.toResponse() }

    private fun Account.toResponse() = AccountResponse(id, userId, bankId, name, type, createdAt)
}

@Service
class CreditCardUseCase(private val creditCardRepository: CreditCardRepository) {
    fun createCreditCard(userId: UUID, req: CreateCreditCardRequest): CreditCardResponse {
        val card = CreditCard(UUID.randomUUID(), userId, req.bankId, req.name, req.lastFourDigits, req.limitAmount, req.closingDay, req.dueDay, true)
        return creditCardRepository.save(card).toResponse()
    }

    fun getCreditCards(userId: UUID): List<CreditCardResponse> = creditCardRepository.findAllByUserId(userId).map { it.toResponse() }

    private fun CreditCard.toResponse() = CreditCardResponse(id, userId, bankId, name, lastFourDigits, limitAmount, closingDay, dueDay, active)
}

@Service
class CategoryUseCase(private val categoryRepository: CategoryRepository) {
    fun createCategory(userId: UUID, req: CreateCategoryRequest): CategoryResponse {
        val cat = Category(UUID.randomUUID(), userId, req.name, req.type, req.color, req.icon, true)
        return categoryRepository.save(cat).toResponse()
    }

    fun getCategories(userId: UUID): List<CategoryResponse> = categoryRepository.findAllByUserId(userId).map { it.toResponse() }

    private fun Category.toResponse() = CategoryResponse(id, userId, name, type, color, icon, active)
}

@Service
class TransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val bankRepository: BankRepository
) {
    fun createTransaction(userId: UUID, req: CreateTransactionRequest): TransactionResponse {
        val tx = Transaction(
            UUID.randomUUID(), userId, req.bankId, req.accountId, req.creditCardId,
            req.categoryId, req.date, req.description, req.merchant, req.amount,
            req.type, req.paymentMethod, TransactionSource.MANUAL,
            req.installmentNumber, req.installmentTotal, req.recurring, LocalDateTime.now()
        )
        return transactionRepository.save(tx).toResponse()
    }

    fun getTransactions(userId: UUID, yearMonth: String?, bankId: UUID?, categoryId: UUID?): List<TransactionResponse> {
        val ym = yearMonth ?: YearMonth.now().toString()
        return transactionRepository.findAllByUserIdAndYearMonth(userId, ym, bankId, categoryId).map { it.toResponse() }
    }

    fun updateTransaction(userId: UUID, transactionId: UUID, req: UpdateTransactionRequest): TransactionResponse {
        val existing = transactionRepository.findById(transactionId) ?: throw NoSuchElementException("Transaction not found")
        val updated = existing.copy(
            categoryId = req.categoryId ?: existing.categoryId,
            description = req.description ?: existing.description,
            merchant = req.merchant ?: existing.merchant,
            amount = req.amount ?: existing.amount,
            date = req.date ?: existing.date
        )
        return transactionRepository.update(updated).toResponse()
    }

    fun deleteTransaction(userId: UUID, transactionId: UUID) = transactionRepository.delete(transactionId)

    private fun Transaction.toResponse(): TransactionResponse {
        val category = categoryRepository.findById(categoryId)
        val bank = bankId?.let { bankRepository.findById(it) }
        return TransactionResponse(
            id, userId, bankId, accountId, creditCardId, categoryId,
            category?.name, category?.color, bank?.name,
            date, description, merchant, amount, type, paymentMethod, source,
            installmentNumber, installmentTotal, recurring, createdAt
        )
    }
}

@Service
class BudgetUseCase(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) {
    fun createBudget(userId: UUID, req: CreateBudgetRequest): BudgetResponse {
        val budget = MonthlyBudget(UUID.randomUUID(), userId, req.categoryId, req.yearMonth, req.limitAmount)
        return budgetRepository.save(budget).toResponse(userId)
    }

    fun getBudgets(userId: UUID, yearMonth: String): List<BudgetResponse> =
        budgetRepository.findAllByUserIdAndYearMonth(userId, yearMonth).map { it.toResponse(userId) }

    private fun MonthlyBudget.toResponse(userId: UUID): BudgetResponse {
        val category = categoryRepository.findById(categoryId)
        val ym = YearMonth.parse(yearMonth)
        val transactions = transactionRepository.findByUserIdAndDateBetween(
            userId, ym.atDay(1), ym.atEndOfMonth()
        )
        val spent = transactions
            .filter { it.categoryId == categoryId && it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        return BudgetResponse(id, userId, categoryId, category?.name, yearMonth, limitAmount, spent)
    }
}

@Service
class DashboardUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val bankRepository: BankRepository,
    private val budgetRepository: BudgetRepository
) {
    fun getDashboard(userId: UUID, yearMonth: String): DashboardResponse {
        val ym = YearMonth.parse(yearMonth)
        val start = ym.atDay(1)
        val end = ym.atEndOfMonth()

        val transactions = transactionRepository.findByUserIdAndDateBetween(userId, start, end)
        val categories = categoryRepository.findAllByUserId(userId).associateBy { it.id }
        val banks = bankRepository.findAllByUserId(userId).associateBy { it.id }

        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        val incomes = transactions.filter { it.type == TransactionType.INCOME }

        val totalExpense = expenses.sumOf { it.amount }
        val totalIncome = incomes.sumOf { it.amount }

        val expensesByCategory = expenses
            .groupBy { it.categoryId }
            .map { (catId, txs) ->
                val cat = categories[catId]
                ExpenseByCategory(catId, cat?.name ?: "Outros", cat?.color ?: "#888", txs.sumOf { it.amount })
            }.sortedByDescending { it.total }

        val expensesByBank = expenses
            .groupBy { it.bankId }
            .map { (bankId, txs) ->
                val bank = bankId?.let { banks[it] }
                ExpenseByBank(bankId, bank?.name ?: "Outros", txs.sumOf { it.amount })
            }.sortedByDescending { it.total }

        val expensesByPaymentMethod = expenses
            .groupBy { it.paymentMethod }
            .map { (method, txs) -> ExpenseByPaymentMethod(method, txs.sumOf { it.amount }) }
            .sortedByDescending { it.total }

        val dailyExpenses = expenses
            .groupBy { it.date }
            .map { (date, txs) -> DailyExpense(date, txs.sumOf { it.amount }) }
            .sortedBy { it.date }

        val topMerchants = expenses
            .filter { !it.merchant.isNullOrBlank() }
            .groupBy { it.merchant!! }
            .map { (merchant, txs) -> TopMerchant(merchant, txs.sumOf { it.amount }, txs.size) }
            .sortedByDescending { it.total }
            .take(10)

        val budgets = budgetRepository.findAllByUserIdAndYearMonth(userId, yearMonth)
        val budgetComparison = budgets.map { budget ->
            val cat = categories[budget.categoryId]
            val spent = expenses.filter { it.categoryId == budget.categoryId }.sumOf { it.amount }
            val pct = if (budget.limitAmount > BigDecimal.ZERO)
                spent.divide(budget.limitAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal(100)).toDouble()
            else 0.0
            BudgetComparison(budget.categoryId, cat?.name ?: "Outros", cat?.color ?: "#888", budget.limitAmount, spent, pct)
        }

        val recentTransactions = transactionRepository.findRecentByUserId(userId, 10).map { tx ->
            val cat = categories[tx.categoryId]
            val bank = tx.bankId?.let { banks[it] }
            TransactionResponse(
                tx.id, tx.userId, tx.bankId, tx.accountId, tx.creditCardId, tx.categoryId,
                cat?.name, cat?.color, bank?.name, tx.date, tx.description, tx.merchant,
                tx.amount, tx.type, tx.paymentMethod, tx.source,
                tx.installmentNumber, tx.installmentTotal, tx.recurring, tx.createdAt
            )
        }

        return DashboardResponse(
            totalIncome, totalExpense, totalIncome - totalExpense,
            expensesByCategory, expensesByBank, expensesByPaymentMethod,
            dailyExpenses, topMerchants, budgetComparison, recentTransactions
        )
    }
}

@Service
class ImportUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val bankRepository: BankRepository,
    private val importFileRepository: ImportFileRepository
) {
    fun importCsv(userId: UUID, file: MultipartFile): Map<String, Any> {
        val lines = file.inputStream.bufferedReader().readLines()
        val header = lines.firstOrNull() ?: throw IllegalArgumentException("Empty file")
        val dataLines = lines.drop(1).filter { it.isNotBlank() }

        val categories = categoryRepository.findAllByUserId(userId).associateBy { it.name.lowercase() }
        val banks = bankRepository.findAllByUserId(userId).associateBy { it.name.lowercase() }

        var imported = 0
        for (line in dataLines) {
            try {
                val parts = line.split(",").map { it.trim() }
                if (parts.size < 6) continue
                val (dateStr, bankName, description, categoryName, amountStr, paymentMethodStr) = parts
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val amount = BigDecimal(amountStr.replace(",", "."))
                val type = if (amount >= BigDecimal.ZERO) TransactionType.INCOME else TransactionType.EXPENSE
                val absAmount = amount.abs()
                val category = categories[categoryName.lowercase()]
                val bank = banks[bankName.lowercase()]
                val paymentMethod = runCatching { PaymentMethod.valueOf(paymentMethodStr.uppercase()) }.getOrDefault(PaymentMethod.PIX)

                val tx = Transaction(
                    UUID.randomUUID(), userId, bank?.id, null, null,
                    category?.id ?: categories.values.firstOrNull()?.id ?: UUID.randomUUID(),
                    date, description, null, absAmount, type, paymentMethod, TransactionSource.CSV_IMPORT,
                    null, null, false, LocalDateTime.now()
                )
                transactionRepository.save(tx)
                imported++
            } catch (e: Exception) {
                // skip invalid line
            }
        }

        val importFile = ImportFile(UUID.randomUUID(), userId, file.originalFilename ?: "import.csv", ImportFileType.CSV, LocalDateTime.now(), imported)
        importFileRepository.save(importFile)

        return mapOf("imported" to imported, "total" to dataLines.size)
    }
}
