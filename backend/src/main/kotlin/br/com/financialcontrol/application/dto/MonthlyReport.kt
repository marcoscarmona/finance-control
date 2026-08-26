package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.enum.SubscriptionFrequency
import br.com.financialcontrol.domain.model.ExpenseInstallment
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
)

data class SubscriptionForecast(
    val name: String,
    val amount: BigDecimal,
    val frequency: SubscriptionFrequency,
)

data class MonthlyReport(
    val totalExpenses: BigDecimal,
    val expensesByCategory: List<CategoryTotal>,
    val cardInvoices: List<CardTotal>,
    val activeSubscriptions: List<SubscriptionForecast>,
    val upcomingInstallments: List<ExpenseInstallment>,
)
