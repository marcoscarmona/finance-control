package br.com.financialcontrol.domain.model

import br.com.financialcontrol.domain.enum.ExpenseStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class ExpenseInstallment(
    val id: UUID,
    val expenseId: UUID,
    val invoiceId: UUID?,
    val number: Int,
    val total: Int,
    val amount: BigDecimal,
    val dueDate: LocalDate,
    val status: ExpenseStatus = ExpenseStatus.PENDING,
)
