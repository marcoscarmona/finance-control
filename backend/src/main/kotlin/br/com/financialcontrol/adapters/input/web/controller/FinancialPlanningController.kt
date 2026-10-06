@file:Suppress("ktlint:standard:max-line-length")

package br.com.financialcontrol.adapters.input.web.controller

import br.com.financialcontrol.adapters.output.persistence.entity.InvestmentGoalJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.InvestmentPositionJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.IncomeForecastJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.ReceivableJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.RecurringExpenseJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.ExchangeRateJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.PersonalLoanJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.SharedExpenseJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.InvestmentGoalRepository
import br.com.financialcontrol.adapters.output.persistence.repository.InvestmentPositionRepository
import br.com.financialcontrol.adapters.output.persistence.repository.IncomeForecastRepository
import br.com.financialcontrol.adapters.output.persistence.repository.ReceivableRepository
import br.com.financialcontrol.adapters.output.persistence.repository.RecurringExpenseRepository
import br.com.financialcontrol.adapters.output.persistence.repository.UserSpringDataRepository
import br.com.financialcontrol.adapters.output.persistence.repository.ExchangeRateRepository
import br.com.financialcontrol.adapters.output.persistence.repository.PersonalLoanRepository
import br.com.financialcontrol.adapters.output.persistence.repository.SharedExpenseRepository
import br.com.financialcontrol.application.dto.CreateExpenseCommand
import br.com.financialcontrol.application.port.input.CreateExpenseUseCase
import br.com.financialcontrol.application.port.input.GenerateMonthlyReportUseCase
import br.com.financialcontrol.domain.enum.PaymentMethod
import br.com.financialcontrol.domain.enum.ReceivableStatus
import br.com.financialcontrol.domain.enum.RecurringExpenseKind
import br.com.financialcontrol.domain.enum.SubscriptionFrequency
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.UUID

@RestController
@RequestMapping("/api/users/{userId}/planning")
class FinancialPlanningController(
    private val users: UserSpringDataRepository,
    private val positions: InvestmentPositionRepository,
    private val incomes: IncomeForecastRepository,
    private val goals: InvestmentGoalRepository,
    private val receivables: ReceivableRepository,
    private val recurring: RecurringExpenseRepository,
    private val loans: PersonalLoanRepository,
    private val sharedExpenses: SharedExpenseRepository,
    private val exchangeRates: ExchangeRateRepository,
    private val createExpense: CreateExpenseUseCase,
    private val reports: GenerateMonthlyReportUseCase,
) {
    @GetMapping("/investments")
    fun investments(@PathVariable userId: UUID, @RequestParam year: Int, @RequestParam month: Int): List<InvestmentPositionJpaEntity> {
        requireUser(userId)
        return positions.findAllByUserIdAndReferenceMonth(userId, YearMonth.of(year, month).toString())
    }

    @PutMapping("/investments")
    fun saveInvestment(@PathVariable userId: UUID, @RequestBody body: InvestmentPositionRequest): InvestmentPositionJpaEntity {
        requireUser(userId)
        require(body.amount >= BigDecimal.ZERO) { "O saldo não pode ser negativo." }
        val reference = YearMonth.of(body.year, body.month).toString()
        val existing = positions.findAllByUserIdAndReferenceMonth(userId, reference).firstOrNull { it.institution.equals(body.institution.trim(), true) }
        return positions.save((existing ?: InvestmentPositionJpaEntity(userId = userId)).also { it.institution = body.institution.trim(); it.referenceMonth = reference; it.amount = body.amount; it.availableForPayments = body.availableForPayments })
    }

    @PutMapping("/investments/{id}")
    fun updateInvestment(@PathVariable userId: UUID, @PathVariable id: UUID, @RequestBody body: InvestmentPositionRequest): InvestmentPositionJpaEntity {
        requireUser(userId)
        require(body.amount >= BigDecimal.ZERO) { "O saldo não pode ser negativo." }
        return positions.findById(id).filter { it.userId == userId }.orElseThrow { IllegalArgumentException("Posição não encontrada.") }.also {
            it.institution = body.institution.trim(); it.referenceMonth = YearMonth.of(body.year, body.month).toString(); it.amount = body.amount; it.availableForPayments = body.availableForPayments
        }.let(positions::save)
    }

    @PutMapping("/investments/{id}/cancel")
    fun removeInvestment(@PathVariable userId: UUID, @PathVariable id: UUID) {
        requireUser(userId)
        val item = positions.findById(id).filter { it.userId == userId }.orElseThrow { IllegalArgumentException("Posição não encontrada.") }
        positions.delete(item)
    }

    @GetMapping("/investment-goal")
    fun goal(@PathVariable userId: UUID): InvestmentGoalJpaEntity? { requireUser(userId); return goals.findByUserId(userId) }

    @PutMapping("/investment-goal")
    fun saveGoal(@PathVariable userId: UUID, @RequestBody body: InvestmentGoalRequest): InvestmentGoalJpaEntity {
        requireUser(userId)
        require(body.targetAmount > BigDecimal.ZERO) { "A meta deve ser maior que zero." }
        return goals.save((goals.findByUserId(userId) ?: InvestmentGoalJpaEntity(userId = userId)).also { it.targetAmount = body.targetAmount; it.targetDate = body.targetDate })
    }

    @GetMapping("/receivables")
    fun listReceivables(@PathVariable userId: UUID): List<ReceivableJpaEntity> { requireUser(userId); return receivables.findAllByUserId(userId) }

    @PostMapping("/receivables") @ResponseStatus(HttpStatus.CREATED)
    fun createReceivable(@PathVariable userId: UUID, @RequestBody body: ReceivableRequest): ReceivableJpaEntity {
        requireUser(userId); require(body.amount > BigDecimal.ZERO) { "O valor deve ser maior que zero." }
        return receivables.save(ReceivableJpaEntity(userId = userId, personName = body.personName.trim(), description = body.description.trim(), amount = body.amount, dueDate = body.dueDate))
    }

    @GetMapping("/income-forecasts")
    fun listIncome(@PathVariable userId: UUID, @RequestParam year: Int, @RequestParam month: Int) = incomes.findAllByUserIdAndReferenceMonth(userId, YearMonth.of(year, month).toString())

    @PutMapping("/income-forecasts")
    fun saveIncome(@PathVariable userId: UUID, @RequestBody body: IncomeForecastRequest): IncomeForecastJpaEntity {
        requireUser(userId); require(body.amount > BigDecimal.ZERO) { "A entrada deve ser maior que zero." }
        val reference = YearMonth.of(body.year, body.month).toString()
        val existing = incomes.findAllByUserIdAndReferenceMonth(userId, reference).firstOrNull { it.sourceName.equals(body.sourceName.trim(), true) }
        return incomes.save((existing ?: IncomeForecastJpaEntity(userId = userId)).also { it.sourceName = body.sourceName.trim(); it.referenceMonth = reference; it.amount = body.amount })
    }

    @GetMapping("/projection")
    fun projection(@PathVariable userId: UUID, @RequestParam startYear: Int, @RequestParam startMonth: Int, @RequestParam endYear: Int, @RequestParam endMonth: Int): List<CashProjectionRow> {
        requireUser(userId)
        val start = YearMonth.of(startYear, startMonth); val end = YearMonth.of(endYear, endMonth)
        require(!end.isBefore(start)) { "O fim deve ser posterior ao início." }
        val allPositions = positions.findAllByUserId(userId); var available = allPositions.filter { it.referenceMonth == start.toString() && it.availableForPayments }.sumOf { it.amount }; var patrimony = allPositions.filter { it.referenceMonth == start.toString() }.sumOf { it.amount }
        return generateSequence(start) { if (it < end) it.plusMonths(1) else null }.map { month ->
            val snapshot = allPositions.filter { it.referenceMonth == month.toString() }
            if (snapshot.isNotEmpty()) { available = snapshot.filter { it.availableForPayments }.sumOf { it.amount }; patrimony = snapshot.sumOf { it.amount } }
            val income = incomes.findAllByUserIdAndReferenceMonth(userId, month.toString()).sumOf { it.amount }
            val receivable = receivables.findAllByUserId(userId).filter { it.status == ReceivableStatus.PENDING && it.dueDate?.let { date -> YearMonth.from(date) == month } == true }.sumOf { it.amount }
                .add(sharedExpenses.findAllByUserId(userId).filter { it.status == "PENDING" && it.referenceMonth == month.toString() }.sumOf { it.counterpartAmount })
            val fixed = recurring.findAllByUserId(userId).filter { it.active && it.kind == RecurringExpenseKind.FIXED_EXPENSE && !month.atEndOfMonth().isBefore(it.startDate) }.sumOf { it.amount }
            val loanPayments = loans.findAllByUserId(userId).filter { it.status == "ACTIVE" }.sumOf { loanInstallmentForMonth(it, month) }
            val payments = reports.execute(userId, month).totalExpenses.add(fixed).add(loanPayments); val opening = available; available = available.add(income).add(receivable).subtract(payments); patrimony = patrimony.add(income).add(receivable).subtract(payments)
            CashProjectionRow(month.toString(), opening, income, receivable, payments, available, patrimony)
        }.toList()
    }

    @PutMapping("/receivables/{id}/receive")
    fun receive(@PathVariable userId: UUID, @PathVariable id: UUID): ReceivableJpaEntity = changeReceivable(userId, id, ReceivableStatus.RECEIVED)

    @PutMapping("/receivables/{id}/cancel")
    fun cancel(@PathVariable userId: UUID, @PathVariable id: UUID): ReceivableJpaEntity = changeReceivable(userId, id, ReceivableStatus.CANCELED)

    @PutMapping("/receivables/{id}")
    fun updateReceivable(@PathVariable userId: UUID, @PathVariable id: UUID, @RequestBody body: ReceivableRequest): ReceivableJpaEntity {
        require(body.amount > BigDecimal.ZERO) { "O valor deve ser maior que zero." }
        return receivables.findById(id).filter { it.userId == userId }.orElseThrow { IllegalArgumentException("Valor a receber não encontrado.") }.also {
            it.personName = body.personName.trim(); it.description = body.description.trim(); it.amount = body.amount; it.dueDate = body.dueDate
        }.let(receivables::save)
    }

    @GetMapping("/recurring-expenses")
    fun listRecurring(@PathVariable userId: UUID): List<RecurringExpenseJpaEntity> { requireUser(userId); return recurring.findAllByUserId(userId) }

    @PostMapping("/recurring-expenses")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRecurring(@PathVariable userId: UUID, @RequestBody body: RecurringExpenseRequest): RecurringExpenseJpaEntity {
        requireUser(userId); require(body.amount > BigDecimal.ZERO) { "O valor deve ser maior que zero." }
        require(body.creditCardId != null || body.accountId != null) { "Informe cartão ou conta." }
        return recurring.save(
            RecurringExpenseJpaEntity(
                userId = userId,
                categoryId = body.categoryId,
                creditCardId = body.creditCardId,
                accountId = body.accountId,
                name = body.name.trim(),
                amount = body.amount,
                kind = body.kind,
                frequency = body.frequency,
                chargeDay = body.chargeDay,
                startDate = body.startDate,
            ),
        )
    }

    @PutMapping("/recurring-expenses/{id}/deactivate")
    fun deactivate(@PathVariable userId: UUID, @PathVariable id: UUID): RecurringExpenseJpaEntity = findRecurring(userId, id).also { it.active = false }.let(recurring::save)

    @PutMapping("/recurring-expenses/{id}")
    fun updateRecurring(@PathVariable userId: UUID, @PathVariable id: UUID, @RequestBody body: RecurringExpenseRequest): RecurringExpenseJpaEntity = findRecurring(userId, id).also {
        it.categoryId = body.categoryId; it.creditCardId = body.creditCardId; it.accountId = body.accountId; it.name = body.name.trim(); it.amount = body.amount; it.kind = body.kind; it.frequency = body.frequency; it.chargeDay = body.chargeDay; it.startDate = body.startDate
    }.let(recurring::save)

    @PostMapping("/recurring-expenses/{id}/confirm")
    fun confirm(@PathVariable userId: UUID, @PathVariable id: UUID, @RequestBody body: ConfirmRecurringRequest) = findRecurring(userId, id).let { item ->
        val date = YearMonth.of(body.year, body.month).atDay(item.chargeDay.coerceAtMost(YearMonth.of(body.year, body.month).lengthOfMonth()))
        createExpense.execute(userId, CreateExpenseCommand(item.categoryId, item.accountId, item.creditCardId, null, item.name, item.name, date, item.amount, if (item.creditCardId != null) PaymentMethod.CREDIT_CARD else PaymentMethod.PIX, 1))
    }

    @GetMapping("/loans")
    fun listLoans(@PathVariable userId: UUID): List<PersonalLoanJpaEntity> { requireUser(userId); return loans.findAllByUserId(userId) }

    @PostMapping("/loans") @ResponseStatus(HttpStatus.CREATED)
    fun createLoan(@PathVariable userId: UUID, @RequestBody body: PersonalLoanRequest): PersonalLoanJpaEntity {
        requireUser(userId); validateLoan(body)
        return loans.save(PersonalLoanJpaEntity(userId = userId, personName = body.personName.trim(), description = body.description.trim(), originalAmount = body.originalAmount, installmentAmount = body.installmentAmount, installmentsTotal = body.installmentsTotal, installmentsPaid = body.installmentsPaid, startDate = body.startDate))
    }

    @PutMapping("/loans/{id}")
    fun updateLoan(@PathVariable userId: UUID, @PathVariable id: UUID, @RequestBody body: PersonalLoanRequest): PersonalLoanJpaEntity {
        validateLoan(body)
        return findLoan(userId, id).also { it.personName = body.personName.trim(); it.description = body.description.trim(); it.originalAmount = body.originalAmount; it.installmentAmount = body.installmentAmount; it.installmentsTotal = body.installmentsTotal; it.installmentsPaid = body.installmentsPaid; it.startDate = body.startDate }.let(loans::save)
    }

    @PutMapping("/loans/{id}/cancel")
    fun cancelLoan(@PathVariable userId: UUID, @PathVariable id: UUID): PersonalLoanJpaEntity = findLoan(userId, id).also { it.status = "CANCELED" }.let(loans::save)

    @GetMapping("/shared-expenses")
    fun listSharedExpenses(@PathVariable userId: UUID): List<SharedExpenseJpaEntity> { requireUser(userId); return sharedExpenses.findAllByUserId(userId) }

    @PostMapping("/shared-expenses") @ResponseStatus(HttpStatus.CREATED)
    fun createSharedExpense(@PathVariable userId: UUID, @RequestBody body: SharedExpenseRequest): SharedExpenseJpaEntity {
        requireUser(userId); validateShared(body)
        return sharedExpenses.save(SharedExpenseJpaEntity(userId = userId, personName = body.personName.trim(), description = body.description.trim(), referenceMonth = YearMonth.of(body.year, body.month).toString(), totalAmount = body.totalAmount, ownAmount = body.ownAmount, counterpartAmount = body.counterpartAmount))
    }

    @PutMapping("/shared-expenses/{id}")
    fun updateSharedExpense(@PathVariable userId: UUID, @PathVariable id: UUID, @RequestBody body: SharedExpenseRequest): SharedExpenseJpaEntity {
        validateShared(body)
        return findSharedExpense(userId, id).also { it.personName = body.personName.trim(); it.description = body.description.trim(); it.referenceMonth = YearMonth.of(body.year, body.month).toString(); it.totalAmount = body.totalAmount; it.ownAmount = body.ownAmount; it.counterpartAmount = body.counterpartAmount }.let(sharedExpenses::save)
    }

    @PutMapping("/shared-expenses/{id}/receive")
    fun receiveSharedExpense(@PathVariable userId: UUID, @PathVariable id: UUID): SharedExpenseJpaEntity = findSharedExpense(userId, id).also { it.status = "RECEIVED" }.let(sharedExpenses::save)

    @PutMapping("/shared-expenses/{id}/cancel")
    fun cancelSharedExpense(@PathVariable userId: UUID, @PathVariable id: UUID): SharedExpenseJpaEntity = findSharedExpense(userId, id).also { it.status = "CANCELED" }.let(sharedExpenses::save)

    @GetMapping("/exchange-rates")
    fun listExchangeRates(@PathVariable userId: UUID, @RequestParam year: Int, @RequestParam month: Int): List<ExchangeRateJpaEntity> { requireUser(userId); return exchangeRates.findAllByUserIdAndReferenceMonth(userId, YearMonth.of(year, month).toString()) }

    @PutMapping("/exchange-rates")
    fun saveExchangeRate(@PathVariable userId: UUID, @RequestBody body: ExchangeRateRequest): ExchangeRateJpaEntity {
        requireUser(userId); require(body.rate > BigDecimal.ZERO) { "A cotação deve ser maior que zero." }
        val reference = YearMonth.of(body.year, body.month).toString()
        val existing = exchangeRates.findAllByUserIdAndReferenceMonth(userId, reference).firstOrNull { it.currency.equals(body.currency.trim(), true) }
        return exchangeRates.save((existing ?: ExchangeRateJpaEntity(userId = userId)).also { it.referenceMonth = reference; it.currency = body.currency.trim().uppercase(); it.rate = body.rate })
    }

    private fun requireUser(userId: UUID) { require(users.existsById(userId)) { "Usuário não encontrado." } }
    private fun findRecurring(userId: UUID, id: UUID) = recurring.findById(id).filter { it.userId == userId }.orElseThrow { IllegalArgumentException("Recorrência não encontrada.") }
    private fun changeReceivable(userId: UUID, id: UUID, status: ReceivableStatus) = receivables.findById(id).filter { it.userId == userId }.orElseThrow { IllegalArgumentException("Valor a receber não encontrado.") }.also { it.status = status; it.receivedAt = if (status == ReceivableStatus.RECEIVED) LocalDate.now() else null }.let(receivables::save)
    private fun findLoan(userId: UUID, id: UUID) = loans.findById(id).filter { it.userId == userId }.orElseThrow { IllegalArgumentException("Empréstimo não encontrado.") }
    private fun findSharedExpense(userId: UUID, id: UUID) = sharedExpenses.findById(id).filter { it.userId == userId }.orElseThrow { IllegalArgumentException("Despesa compartilhada não encontrada.") }
    private fun validateLoan(body: PersonalLoanRequest) { require(body.originalAmount > BigDecimal.ZERO && body.installmentAmount > BigDecimal.ZERO) { "Os valores devem ser maiores que zero." }; require(body.installmentsTotal > 0 && body.installmentsPaid in 0..body.installmentsTotal) { "Parcelas inválidas." } }
    private fun validateShared(body: SharedExpenseRequest) { require(body.totalAmount > BigDecimal.ZERO && body.ownAmount >= BigDecimal.ZERO && body.counterpartAmount >= BigDecimal.ZERO) { "Valores inválidos." }; require(body.ownAmount.add(body.counterpartAmount) <= body.totalAmount) { "O rateio não pode ultrapassar o total." } }
    private fun loanInstallmentForMonth(loan: PersonalLoanJpaEntity, month: YearMonth): BigDecimal {
        val installment = ChronoUnit.MONTHS.between(YearMonth.from(loan.startDate), month).toInt() + 1
        return if (installment in (loan.installmentsPaid + 1)..loan.installmentsTotal) loan.installmentAmount else BigDecimal.ZERO
    }
}

data class InvestmentPositionRequest(val institution: String, val year: Int, val month: Int, val amount: BigDecimal, val availableForPayments: Boolean = false)
data class InvestmentGoalRequest(val targetAmount: BigDecimal, val targetDate: LocalDate)
data class ReceivableRequest(val personName: String, val description: String, val amount: BigDecimal, val dueDate: LocalDate? = null)
data class IncomeForecastRequest(val sourceName: String, val year: Int, val month: Int, val amount: BigDecimal)
data class CashProjectionRow(val month: String, val openingAvailable: BigDecimal, val expectedIncome: BigDecimal, val receivables: BigDecimal, val payments: BigDecimal, val closingAvailable: BigDecimal, val projectedPatrimony: BigDecimal)
data class RecurringExpenseRequest(val categoryId: UUID, val creditCardId: UUID?, val accountId: UUID?, val name: String, val amount: BigDecimal, val kind: RecurringExpenseKind, val frequency: SubscriptionFrequency, val chargeDay: Int, val startDate: LocalDate)
data class ConfirmRecurringRequest(val year: Int, val month: Int)
data class PersonalLoanRequest(val personName: String, val description: String, val originalAmount: BigDecimal, val installmentAmount: BigDecimal, val installmentsTotal: Int, val installmentsPaid: Int, val startDate: LocalDate)
data class SharedExpenseRequest(val personName: String, val description: String, val year: Int, val month: Int, val totalAmount: BigDecimal, val ownAmount: BigDecimal, val counterpartAmount: BigDecimal)
data class ExchangeRateRequest(val currency: String = "USD", val year: Int, val month: Int, val rate: BigDecimal)
