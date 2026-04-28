package br.com.financialcontrol.interfaces.web.controllers

import br.com.financialcontrol.application.dto.*
import br.com.financialcontrol.application.usecases.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(private val useCase: UserUseCase) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody req: CreateUserRequest) = useCase.createUser(req)

    @GetMapping("/demo")
    fun getDemo() = useCase.getDemoUser()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = useCase.getUser(id)
}

@RestController
@RequestMapping("/api/users/{userId}/banks")
class BankController(private val useCase: BankUseCase) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable userId: UUID, @RequestBody req: CreateBankRequest) =
        useCase.createBank(userId, req)

    @GetMapping
    fun getAll(@PathVariable userId: UUID) = useCase.getBanks(userId)
}

@RestController
@RequestMapping("/api/users/{userId}/accounts")
class AccountController(private val useCase: AccountUseCase) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable userId: UUID, @RequestBody req: CreateAccountRequest) =
        useCase.createAccount(userId, req)

    @GetMapping
    fun getAll(@PathVariable userId: UUID) = useCase.getAccounts(userId)
}

@RestController
@RequestMapping("/api/users/{userId}/credit-cards")
class CreditCardController(private val useCase: CreditCardUseCase) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable userId: UUID, @RequestBody req: CreateCreditCardRequest) =
        useCase.createCreditCard(userId, req)

    @GetMapping
    fun getAll(@PathVariable userId: UUID) = useCase.getCreditCards(userId)
}

@RestController
@RequestMapping("/api/users/{userId}/categories")
class CategoryController(private val useCase: CategoryUseCase) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable userId: UUID, @RequestBody req: CreateCategoryRequest) =
        useCase.createCategory(userId, req)

    @GetMapping
    fun getAll(@PathVariable userId: UUID) = useCase.getCategories(userId)
}

@RestController
@RequestMapping("/api/users/{userId}/transactions")
class TransactionController(private val useCase: TransactionUseCase) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable userId: UUID, @RequestBody req: CreateTransactionRequest) =
        useCase.createTransaction(userId, req)

    @GetMapping
    fun getAll(
        @PathVariable userId: UUID,
        @RequestParam(required = false) yearMonth: String?,
        @RequestParam(required = false) bankId: UUID?,
        @RequestParam(required = false) categoryId: UUID?
    ) = useCase.getTransactions(userId, yearMonth, bankId, categoryId)

    @PatchMapping("/{transactionId}")
    fun update(
        @PathVariable userId: UUID,
        @PathVariable transactionId: UUID,
        @RequestBody req: UpdateTransactionRequest
    ) = useCase.updateTransaction(userId, transactionId, req)

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable userId: UUID, @PathVariable transactionId: UUID) =
        useCase.deleteTransaction(userId, transactionId)
}

@RestController
@RequestMapping("/api/users/{userId}/budgets")
class BudgetController(private val useCase: BudgetUseCase) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@PathVariable userId: UUID, @RequestBody req: CreateBudgetRequest) =
        useCase.createBudget(userId, req)

    @GetMapping
    fun getAll(
        @PathVariable userId: UUID,
        @RequestParam(defaultValue = "") yearMonth: String
    ): List<BudgetResponse> {
        val ym = yearMonth.ifBlank { java.time.YearMonth.now().toString() }
        return useCase.getBudgets(userId, ym)
    }
}

@RestController
@RequestMapping("/api/users/{userId}/dashboard")
class DashboardController(private val useCase: DashboardUseCase) {

    @GetMapping
    fun getDashboard(
        @PathVariable userId: UUID,
        @RequestParam(defaultValue = "") yearMonth: String
    ): DashboardResponse {
        val ym = yearMonth.ifBlank { java.time.YearMonth.now().toString() }
        return useCase.getDashboard(userId, ym)
    }
}

@RestController
@RequestMapping("/api/users/{userId}/imports")
class ImportController(private val useCase: ImportUseCase) {

    @PostMapping("/csv", consumes = ["multipart/form-data"])
    fun importCsv(
        @PathVariable userId: UUID,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, Any>> {
        val result = useCase.importCsv(userId, file)
        return ResponseEntity.ok(result)
    }
}
