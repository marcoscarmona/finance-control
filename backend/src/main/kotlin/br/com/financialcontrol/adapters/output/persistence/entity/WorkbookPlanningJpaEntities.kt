package br.com.financialcontrol.adapters.output.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity @Table(name = "personal_loans")
class PersonalLoanJpaEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name = "user_id") var userId: UUID = UUID.randomUUID(), @Column(name = "person_name") var personName: String = "", var description: String = "", @Column(name = "original_amount") var originalAmount: BigDecimal = BigDecimal.ZERO, @Column(name = "installment_amount") var installmentAmount: BigDecimal = BigDecimal.ZERO, @Column(name = "installments_total") var installmentsTotal: Int = 1, @Column(name = "installments_paid") var installmentsPaid: Int = 0, @Column(name = "start_date") var startDate: LocalDate = LocalDate.now(), var status: String = "ACTIVE")
@Entity @Table(name = "shared_expenses")
class SharedExpenseJpaEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name = "user_id") var userId: UUID = UUID.randomUUID(), @Column(name = "person_name") var personName: String = "", var description: String = "", @Column(name = "reference_month") var referenceMonth: String = "", @Column(name = "total_amount") var totalAmount: BigDecimal = BigDecimal.ZERO, @Column(name = "own_amount") var ownAmount: BigDecimal = BigDecimal.ZERO, @Column(name = "counterpart_amount") var counterpartAmount: BigDecimal = BigDecimal.ZERO, var status: String = "PENDING")
@Entity @Table(name = "exchange_rates")
class ExchangeRateJpaEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name = "user_id") var userId: UUID = UUID.randomUUID(), @Column(name = "reference_month") var referenceMonth: String = "", var currency: String = "USD", var rate: BigDecimal = BigDecimal.ONE)
