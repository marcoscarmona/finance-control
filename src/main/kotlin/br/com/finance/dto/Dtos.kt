package br.com.finance.dto

import br.com.carmona.finance.model.TransactionType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateTransactionRequest(
    @field:NotNull val date: LocalDate,
    @field:NotBlank val bank: String,
    val card: String? = null,
    @field:NotBlank val description: String,
    val merchant: String? = null,
    val categoryId: UUID? = null,
    @field:NotNull val amount: BigDecimal,
    val type: TransactionType = TransactionType.DEBIT,
    val installmentNumber: Int? = null,
    val installmentTotal: Int? = null,
    val source: String? = "manual"
)

data class TransactionResponse(
    val id: UUID,
    val date: LocalDate,
    val month: String,
    val bank: String,
    val card: String?,
    val description: String,
    val merchant: String?,
    val category: String?,
    val amount: BigDecimal,
    val type: TransactionType,
    val installmentNumber: Int?,
    val installmentTotal: Int?
)

data class CategoryRequest(
    @field:NotBlank val name: String,
    @field:PositiveOrZero val monthlyLimit: BigDecimal? = null
)

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val monthlyLimit: BigDecimal?
)

data class MerchantRuleRequest(
    @field:NotBlank val keyword: String,
    @field:NotNull val categoryId: UUID,
    val priority: Int = 100
)

data class MerchantRuleResponse(
    val id: UUID,
    val keyword: String,
    val category: String,
    val priority: Int
)

data class DashboardResponse(
    val month: String,
    val totalDebit: BigDecimal,
    val totalCredit: BigDecimal,
    val netTotal: BigDecimal,
    val byCategory: List<SummaryItem>,
    val byBank: List<SummaryItem>,
    val topMerchants: List<SummaryItem>,
    val alerts: List<String>
)

data class SummaryItem(
    val name: String,
    val amount: BigDecimal
)
