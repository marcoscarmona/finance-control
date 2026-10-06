package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.enum.SubscriptionFrequency
import java.math.BigDecimal
import java.util.UUID

data class CategoryTotal(
    val category: String,
    val total: BigDecimal,
)

data class CardTotal(
    val cardId: UUID,
    val cardName: String,
    val total: BigDecimal,
    val detailedTotal: BigDecimal,
    val manualTotal: BigDecimal?,
    val adjustment: BigDecimal,
    val amountDue: BigDecimal,
    val purchasesTotal: BigDecimal,
    val paymentsAndCredits: BigDecimal,
    val unclassifiedPurchases: BigDecimal,
    val futureInstallmentsTotal: BigDecimal,
    val categories: List<CategoryTotal>,
)

data class UpcomingInstallment(
    val cardId: UUID,
    val cardName: String,
    val description: String,
    val category: String,
    val number: Int,
    val total: Int,
    val amount: BigDecimal,
    val dueDate: java.time.LocalDate,
)

data class UpcomingInstallmentsByCard(
    val cardId: UUID,
    val cardName: String,
    val total: BigDecimal,
    val installments: List<UpcomingInstallment>,
)

data class SubscriptionForecast(
    val name: String,
    val amount: BigDecimal,
    val frequency: SubscriptionFrequency,
    val chargeDay: Int,
    val category: String,
    val paymentSource: String,
)

data class MonthlyReport(
    val totalExpenses: BigDecimal,
    val expensesByCategory: List<CategoryTotal>,
    val cardInvoices: List<CardTotal>,
    val activeSubscriptions: List<SubscriptionForecast>,
    val upcomingInstallmentsByCard: List<UpcomingInstallmentsByCard>,
)
