package br.com.financialcontrol.interfaces.web

import br.com.financialcontrol.application.*
import br.com.financialcontrol.domain.*
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

data class CreateUserRequest(@field:NotBlank val name: String, @field:Email val email: String)
data class BankRequest(@field:NotBlank val name: String, val code: String? = null, val type: BankType)
data class AccountRequest(val bankId: UUID, @field:NotBlank val name: String, val type: AccountType)
data class CategoryRequest(@field:NotBlank val name: String, @field:Pattern(regexp = "^#[0-9A-Fa-f]{6}$") val color: String = "#888888")
data class CardRequest(val bankId: UUID, @field:NotBlank val name: String, @field:Pattern(regexp = "^[0-9]{4}$") val lastFourDigits: String, @field:DecimalMin("0.01") val limitAmount: BigDecimal, @field:Min(1) @field:Max(31) val closingDay: Int, @field:Min(1) @field:Max(31) val dueDay: Int)
data class SubscriptionRequest(val categoryId: UUID, val creditCardId: UUID? = null, val accountId: UUID? = null, @field:NotBlank val name: String, @field:DecimalMin("0.01") val amount: BigDecimal, val frequency: SubscriptionFrequency, @field:Min(1) @field:Max(31) val chargeDay: Int)
data class ExpenseRequest(val categoryId: UUID, val accountId: UUID? = null, val creditCardId: UUID? = null, val subscriptionId: UUID? = null, @field:NotBlank val description: String, val merchant: String? = null, val purchaseDate: LocalDate, @field:DecimalMin("0.01") val totalAmount: BigDecimal, val paymentMethod: PaymentMethod, @field:Min(1) val installments: Int = 1)
data class ExpenseCreatedResponse(val expense: Expense, val installments: List<ExpenseInstallment>)

@RestController @RequestMapping("/api/users")
class UserController(private val service: FinanceService) {
    @PostMapping @ResponseStatus(HttpStatus.CREATED) fun create(@Valid @RequestBody body: CreateUserRequest) = service.createUser(body.name, body.email)
    @GetMapping("/{userId}") fun get(@PathVariable userId: UUID) = service.user(userId)
}

@RestController @RequestMapping("/api/users/{userId}")
class FinanceController(private val service: FinanceService) {
    @PostMapping("/banks") @ResponseStatus(HttpStatus.CREATED) fun bank(@PathVariable userId: UUID, @Valid @RequestBody body: BankRequest) = service.createBank(userId, body.name, body.code, body.type)
    @GetMapping("/banks") fun banks(@PathVariable userId: UUID) = service.banks(userId)
    @PostMapping("/accounts") @ResponseStatus(HttpStatus.CREATED) fun account(@PathVariable userId: UUID, @Valid @RequestBody body: AccountRequest) = service.createAccount(userId, body.bankId, body.name, body.type)
    @GetMapping("/accounts") fun accounts(@PathVariable userId: UUID) = service.accounts(userId)
    @PostMapping("/categories") @ResponseStatus(HttpStatus.CREATED) fun category(@PathVariable userId: UUID, @Valid @RequestBody body: CategoryRequest) = service.createCategory(userId, body.name, body.color)
    @GetMapping("/categories") fun categories(@PathVariable userId: UUID) = service.categories(userId)
    @PostMapping("/credit-cards") @ResponseStatus(HttpStatus.CREATED) fun card(@PathVariable userId: UUID, @Valid @RequestBody body: CardRequest) = service.createCard(userId, body.bankId, body.name, body.lastFourDigits, body.limitAmount, body.closingDay, body.dueDay)
    @GetMapping("/credit-cards") fun cards(@PathVariable userId: UUID) = service.cards(userId)
    @GetMapping("/credit-cards/{cardId}/invoices/{year}/{month}") fun invoice(@PathVariable userId: UUID, @PathVariable cardId: UUID, @PathVariable year: Int, @PathVariable month: Int) = service.invoice(userId, cardId, YearMonth.of(year, month))
    @PostMapping("/subscriptions") @ResponseStatus(HttpStatus.CREATED) fun subscription(@PathVariable userId: UUID, @Valid @RequestBody body: SubscriptionRequest) = service.createSubscription(userId, body.categoryId, body.creditCardId, body.accountId, body.name, body.amount, body.frequency, body.chargeDay)
    @GetMapping("/subscriptions") fun subscriptions(@PathVariable userId: UUID) = service.subscriptions(userId)
    @PostMapping("/expenses") @ResponseStatus(HttpStatus.CREATED) fun expense(@PathVariable userId: UUID, @Valid @RequestBody body: ExpenseRequest): ExpenseCreatedResponse { val result = service.createExpense(userId, CreateExpenseCommand(body.categoryId, body.accountId, body.creditCardId, body.subscriptionId, body.description, body.merchant, body.purchaseDate, body.totalAmount, body.paymentMethod, body.installments)); return ExpenseCreatedResponse(result.first, result.second) }
    @GetMapping("/expenses") fun expenses(@PathVariable userId: UUID) = service.expenses(userId)
    @GetMapping("/reports/monthly") fun report(@PathVariable userId: UUID, @RequestParam year: Int, @RequestParam month: Int) = service.monthlyReport(userId, YearMonth.of(year, month))
}

@RestControllerAdvice
class ApiErrorHandler { @ExceptionHandler(IllegalArgumentException::class, NoSuchElementException::class) @ResponseStatus(HttpStatus.BAD_REQUEST) fun invalid(error: RuntimeException) = mapOf("message" to (error.message ?: "Invalid request")) }
