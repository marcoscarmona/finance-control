package br.com.financialcontrol.adapters.output.persistence.entity

import br.com.financialcontrol.domain.enum.ReceivableStatus
import br.com.financialcontrol.domain.enum.RecurringExpenseKind
import br.com.financialcontrol.domain.enum.SubscriptionFrequency
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity @Table(name = "investment_positions")
class InvestmentPositionJpaEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name = "user_id") var userId: UUID = UUID.randomUUID(), var institution: String = "", @Column(name = "reference_month") var referenceMonth: String = "", var amount: BigDecimal = BigDecimal.ZERO)
@Entity @Table(name = "investment_goals")
class InvestmentGoalJpaEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name = "user_id") var userId: UUID = UUID.randomUUID(), @Column(name = "target_amount") var targetAmount: BigDecimal = BigDecimal.ZERO, @Column(name = "target_date") var targetDate: LocalDate = LocalDate.now())
@Entity @Table(name = "receivables")
class ReceivableJpaEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name = "user_id") var userId: UUID = UUID.randomUUID(), @Column(name = "person_name") var personName: String = "", var description: String = "", var amount: BigDecimal = BigDecimal.ZERO, @Column(name = "due_date") var dueDate: LocalDate = LocalDate.now(), @Enumerated(EnumType.STRING) var status: ReceivableStatus = ReceivableStatus.PENDING, @Column(name = "received_at") var receivedAt: LocalDate? = null)
@Entity @Table(name = "recurring_expenses")
class RecurringExpenseJpaEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name = "user_id") var userId: UUID = UUID.randomUUID(), @Column(name = "category_id") var categoryId: UUID = UUID.randomUUID(), @Column(name = "credit_card_id") var creditCardId: UUID? = null, @Column(name = "account_id") var accountId: UUID? = null, var name: String = "", var amount: BigDecimal = BigDecimal.ZERO, @Enumerated(EnumType.STRING) var kind: RecurringExpenseKind = RecurringExpenseKind.FIXED_EXPENSE, @Enumerated(EnumType.STRING) var frequency: SubscriptionFrequency = SubscriptionFrequency.MONTHLY, @Column(name = "charge_day") var chargeDay: Int = 1, @Column(name = "start_date") var startDate: LocalDate = LocalDate.now(), var active: Boolean = true)
