package br.com.financialcontrol.adapters.output.persistence.entity

import br.com.financialcontrol.domain.enum.ExpenseStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "expense_installments")
class ExpenseInstallmentJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "expense_id") var expenseId: UUID = UUID.randomUUID(),
    @Column(name = "invoice_id") var invoiceId: UUID? = null,
    var number: Int = 1,
    var total: Int = 1,
    var amount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "due_date") var dueDate: LocalDate = LocalDate.now(),
    @Enumerated(EnumType.STRING) var status: ExpenseStatus = ExpenseStatus.PENDING,
)
