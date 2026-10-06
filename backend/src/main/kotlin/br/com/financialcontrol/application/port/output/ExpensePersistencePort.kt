package br.com.financialcontrol.application.port.output

import br.com.financialcontrol.domain.model.Expense
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

interface ExpensePersistencePort {
    fun save(expense: Expense): Expense

    fun findById(id: UUID): Expense?

    fun findAllByUserId(userId: UUID): List<Expense>

    fun existsByUserIdAndDescriptionAndPurchaseDateAndTotalAmount(
        userId: UUID,
        description: String,
        purchaseDate: LocalDate,
        totalAmount: BigDecimal,
    ): Boolean
}
