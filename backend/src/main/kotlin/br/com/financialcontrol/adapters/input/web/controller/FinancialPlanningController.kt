package br.com.financialcontrol.adapters.input.web.controller

import br.com.financialcontrol.adapters.output.persistence.entity.InvestmentGoalJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.InvestmentPositionJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.ReceivableJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.RecurringExpenseJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.InvestmentGoalRepository
import br.com.financialcontrol.adapters.output.persistence.repository.InvestmentPositionRepository
import br.com.financialcontrol.adapters.output.persistence.repository.ReceivableRepository
import br.com.financialcontrol.adapters.output.persistence.repository.RecurringExpenseRepository
import br.com.financialcontrol.adapters.output.persistence.repository.UserSpringDataRepository
import br.com.financialcontrol.application.dto.CreateExpenseCommand
import br.com.financialcontrol.application.port.input.CreateExpenseUseCase
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
import java.util.UUID

@RestController
@RequestMapping("/api/users/{userId}/planning")
class FinancialPlanningController(
    private val users: UserSpringDataRepository,
    private val positions: InvestmentPositionRepository,
    private val goals: InvestmentGoalRepository,
    private val receivables: ReceivableRepository,
    private val recurring: RecurringExpenseRepository,
    private val createExpense: CreateExpenseUseCase,
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
        return positions.save((existing ?: InvestmentPositionJpaEntity(userId = userId)).also { it.institution = body.institution.trim(); it.referenceMonth = reference; it.amount = body.amount })
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

    @PutMapping("/receivables/{id}/receive")
    fun receive(@PathVariable userId: UUID, @PathVariable id: UUID): ReceivableJpaEntity = changeReceivable(userId, id, ReceivableStatus.RECEIVED)

    @PutMapping("/receivables/{id}/cancel")
    fun cancel(@PathVariable userId: UUID, @PathVariable id: UUID): ReceivableJpaEntity = changeReceivable(userId, id, ReceivableStatus.CANCELED)

    @GetMapping("/recurring-expenses")
    fun listRecurring(@PathVariable userId: UUID): List<RecurringExpenseJpaEntity> { requireUser(userId); return recurring.findAllByUserId(userId) }

    @PostMapping("/recurring-expenses") @ResponseStatus(HttpStatus.CREATED)
    fun createRecurring(@PathVariable userId: UUID, @RequestBody body: RecurringExpenseRequest): RecurringExpenseJpaEntity {
        requireUser(userId); require(body.amount > BigDecimal.ZERO) { "O valor deve ser maior que zero." }
        require(body.creditCardId != null || body.accountId != null) { "Informe cartão ou conta." }
        return recurring.save(RecurringExpenseJpaEntity(userId = userId, categoryId = body.categoryId, creditCardId = body.creditCardId, accountId = body.accountId, name = body.name.trim(), amount = body.amount, kind = body.kind, frequency = body.frequency, chargeDay = body.chargeDay, startDate = body.startDate))
    }

    @PutMapping("/recurring-expenses/{id}/deactivate")
    fun deactivate(@PathVariable userId: UUID, @PathVariable id: UUID): RecurringExpenseJpaEntity = findRecurring(userId, id).also { it.active = false }.let(recurring::save)

    @PostMapping("/recurring-expenses/{id}/confirm")
    fun confirm(@PathVariable userId: UUID, @PathVariable id: UUID, @RequestBody body: ConfirmRecurringRequest) = findRecurring(userId, id).let { item ->
        val date = YearMonth.of(body.year, body.month).atDay(item.chargeDay.coerceAtMost(YearMonth.of(body.year, body.month).lengthOfMonth()))
        createExpense.execute(userId, CreateExpenseCommand(item.categoryId, item.accountId, item.creditCardId, null, item.name, item.name, date, item.amount, if (item.creditCardId != null) PaymentMethod.CREDIT_CARD else PaymentMethod.PIX, 1))
    }

    private fun requireUser(userId: UUID) { require(users.existsById(userId)) { "Usuário não encontrado." } }
    private fun findRecurring(userId: UUID, id: UUID) = recurring.findById(id).filter { it.userId == userId }.orElseThrow { IllegalArgumentException("Recorrência não encontrada.") }
    private fun changeReceivable(userId: UUID, id: UUID, status: ReceivableStatus) = receivables.findById(id).filter { it.userId == userId }.orElseThrow { IllegalArgumentException("Valor a receber não encontrado.") }.also { it.status = status; it.receivedAt = if (status == ReceivableStatus.RECEIVED) LocalDate.now() else null }.let(receivables::save)
}

data class InvestmentPositionRequest(val institution: String, val year: Int, val month: Int, val amount: BigDecimal)
data class InvestmentGoalRequest(val targetAmount: BigDecimal, val targetDate: LocalDate)
data class ReceivableRequest(val personName: String, val description: String, val amount: BigDecimal, val dueDate: LocalDate)
data class RecurringExpenseRequest(val categoryId: UUID, val creditCardId: UUID?, val accountId: UUID?, val name: String, val amount: BigDecimal, val kind: RecurringExpenseKind, val frequency: SubscriptionFrequency, val chargeDay: Int, val startDate: LocalDate)
data class ConfirmRecurringRequest(val year: Int, val month: Int)
