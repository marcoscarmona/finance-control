package br.com.carmona.finance.service

import br.com.carmona.finance.dto.DashboardResponse
import br.com.carmona.finance.dto.SummaryItem
import br.com.carmona.finance.model.TransactionType
import br.com.carmona.finance.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class DashboardService(
    private val transactionRepository: TransactionRepository
) {
    @Transactional(readOnly = true)
    fun getDashboard(month: String): DashboardResponse {
        val transactions = transactionRepository.findByMonthRefOrderByTransactionDateAsc(month)
        val debits = transactions.filter { it.type == TransactionType.DEBIT }.sumOf { it.amount }
        val credits = transactions.filter { it.type == TransactionType.CREDIT }.sumOf { it.amount }
        val categorySummary = transactionRepository.sumByCategory(month).mapRows().take(10)
        val bankSummary = transactionRepository.sumByBank(month).mapRows()
        val topMerchants = transactionRepository.topMerchants(month).mapRows().take(10)

        return DashboardResponse(
            month = month,
            totalDebit = debits,
            totalCredit = credits,
            netTotal = debits.subtract(credits),
            byCategory = categorySummary,
            byBank = bankSummary,
            topMerchants = topMerchants,
            alerts = buildAlerts(categorySummary, debits)
        )
    }

    private fun List<Array<Any>>.mapRows(): List<SummaryItem> = map {
        SummaryItem(
            name = it[0].toString(),
            amount = it[1] as BigDecimal
        )
    }

    private fun buildAlerts(categories: List<SummaryItem>, total: BigDecimal): List<String> {
        if (total == BigDecimal.ZERO) return emptyList()
        val biggest = categories.maxByOrNull { it.amount }
        return listOfNotNull(
            biggest?.let { "Maior vazamento: ${it.name} (${it.amount})" },
            if (total > BigDecimal("10000")) "Gasto mensal acima de R$ 10.000. Revisar compras e delivery." else null,
            categories.firstOrNull { it.name.contains("Alimentação", ignoreCase = true) && it.amount > BigDecimal("3000") }
                ?.let { "Alimentação acima da meta. Cortar delivery durante a semana." }
        )
    }
}
