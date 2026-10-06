package br.com.financialcontrol.infrastructure.importer

import br.com.financialcontrol.application.dto.CreateBankCommand
import br.com.financialcontrol.application.dto.CreateCategoryCommand
import br.com.financialcontrol.application.dto.CreateCreditCardCommand
import br.com.financialcontrol.application.dto.CreateExpenseCommand
import br.com.financialcontrol.application.port.input.CreateBankUseCase
import br.com.financialcontrol.application.port.input.CreateCategoryUseCase
import br.com.financialcontrol.application.port.input.CreateCreditCardUseCase
import br.com.financialcontrol.application.port.input.CreateExpenseUseCase
import br.com.financialcontrol.application.port.output.UserPersistencePort
import br.com.financialcontrol.domain.enum.BankType
import br.com.financialcontrol.domain.enum.CardDateRule
import br.com.financialcontrol.domain.enum.PaymentMethod
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.Normalizer
import java.time.YearMonth
import java.util.UUID

@Component
@ConditionalOnProperty(name = ["finance.nubank.csv-files"])
class NubankCsvImportRunner(
    @Value("\${finance.nubank.csv-files}") private val sourceFiles: String,
    @Value("\${finance.import.email:}") private val email: String,
    private val jdbc: JdbcTemplate,
    private val users: UserPersistencePort,
    private val createBank: CreateBankUseCase,
    private val createCategory: CreateCategoryUseCase,
    private val createCard: CreateCreditCardUseCase,
    private val createExpense: CreateExpenseUseCase,
) : CommandLineRunner {
    @Transactional
    override fun run(vararg args: String) {
        require(email.isNotBlank()) { "finance.import.email is required" }
        val user = requireNotNull(users.findByEmail(email.trim().lowercase())) { "User not found for $email" }
        val statements =
            sourceFiles
                .split('|')
                .map(::File)
                .map { file ->
                    require(file.isFile) { "Nubank CSV not found: ${file.absolutePath}" }
                    Statement(statementMonth(file.name), readRows(file))
                }.sortedBy { it.month }
        resetFinancialData(user.id)
        val bank = createBank.execute(user.id, CreateBankCommand("Nubank", null, BankType.BANK))
        val card =
            createCard.execute(
                user.id,
                CreateCreditCardCommand(
                    bank.id,
                    "Nubank",
                    "9526",
                    BigDecimal("40000.00"),
                    26,
                    5,
                    CardDateRule.FIXED_DAY,
                    CardDateRule.NTH_BUSINESS_DAY_AFTER_CLOSING,
                ),
            )
        val categories =
            listOf(
                CategorySeed("Outros", "#64748B"),
                CategorySeed("Alimentação", "#EAB308"),
                CategorySeed("Farmácia", "#EC4899"),
                CategorySeed("Delivery", "#F97316"),
                CategorySeed("Mercado", "#22C55E"),
            ).associate { seed ->
                seed.name to
                    createCategory.execute(user.id, CreateCategoryCommand(seed.name, seed.color))
            }
        val importedInstallments = mutableSetOf<String>()
        statements.forEach { statement ->
            statement.rows.forEach { row ->
                val installment = INSTALLMENT.find(row.title)
                val merchant =
                    row.title
                        .removeSuffix(installment?.value.orEmpty())
                        .trim()
                        .trimEnd('-', ' ')
                val key = "${merchant.key()}|${row.amount.setScale(2)}|${installment?.groupValues?.get(2).orEmpty()}"
                if (installment != null && !importedInstallments.add(key)) return@forEach
                val number = installment?.groupValues?.get(1)?.toInt() ?: 1
                val total = installment?.groupValues?.get(2)?.toInt() ?: 1
                val purchaseDate = statement.month.minusMonths((number - 1).toLong()).atDay(1)
                val category = categories[categoryName(merchant)] ?: categories.getValue("Outros")
                createExpense.execute(
                    user.id,
                    CreateExpenseCommand(
                        category.id,
                        null,
                        card.id,
                        null,
                        row.title,
                        merchant,
                        purchaseDate,
                        row.amount.multiply(BigDecimal(total)),
                        PaymentMethod.CREDIT_CARD,
                        total,
                    ),
                )
            }
        }
        println("Nubank import completed: ${statements.sumOf { it.rows.size }} statement rows")
    }

    private fun resetFinancialData(userId: UUID) {
        val id = userId.toString()
        jdbc.update("DELETE FROM expense_installments WHERE expense_id IN (SELECT id FROM expenses WHERE user_id = ?::uuid)", id)
        jdbc.update("DELETE FROM card_invoices WHERE credit_card_id IN (SELECT id FROM credit_cards WHERE user_id = ?::uuid)", id)
        jdbc.update("DELETE FROM expenses WHERE user_id = ?::uuid", id)
        jdbc.update("DELETE FROM subscriptions WHERE user_id = ?::uuid", id)
        jdbc.update("DELETE FROM merchant_category_rules WHERE user_id = ?::uuid", id)
        jdbc.update("DELETE FROM credit_cards WHERE user_id = ?::uuid", id)
        jdbc.update("DELETE FROM accounts WHERE user_id = ?::uuid", id)
        jdbc.update("DELETE FROM banks WHERE user_id = ?::uuid", id)
        jdbc.update("DELETE FROM categories WHERE user_id = ?::uuid", id)
    }

    private fun readRows(file: File): List<Row> =
        file.readLines().drop(1).mapNotNull { line ->
            val match = CSV.matchEntire(line) ?: return@mapNotNull null
            val amount =
                match.groupValues[3]
                    .replace(" ", "")
                    .replace(".", "")
                    .replace(',', '.')
                    .toBigDecimalOrNull() ?: return@mapNotNull null
            Row(match.groupValues[2], amount.setScale(2, RoundingMode.HALF_UP))
        }

    private fun statementMonth(fileName: String): YearMonth =
        YearMonth
            .parse(
                Regex("(20\\d{2}-\\d{2})-").find(fileName)?.groupValues?.get(1) ?: error("Invalid Nubank filename: $fileName"),
            ).minusMonths(1)

    private fun categoryName(merchant: String): String =
        when {
            merchant.key().containsAny("raia", "farma", "drogasil") -> "Farmácia"
            merchant.key().containsAny("ifd", "delivery", "food service") -> "Delivery"
            merchant.key().containsAny("mercado", "super", "hortifruti", "emporio", "praca") -> "Mercado"
            merchant.key().containsAny("carnes", "oba") -> "Alimentação"
            else -> "Outros"
        }

    private fun String.key() =
        Normalizer
            .normalize(this, Normalizer.Form.NFD)
            .replace("\\p{M}".toRegex(), "")
            .lowercase()
            .trim()

    private fun String.containsAny(vararg values: String) = values.any(::contains)

    private data class Row(
        val title: String,
        val amount: BigDecimal,
    )

    private data class Statement(
        val month: YearMonth,
        val rows: List<Row>,
    )

    private data class CategorySeed(
        val name: String,
        val color: String,
    )

    private companion object {
        val INSTALLMENT = Regex("\\s*-\\s*Parcela\\s+(\\d+)/(\\d+)", RegexOption.IGNORE_CASE)
        val CSV = Regex("(\\d{4}-\\d{2}-\\d{2}),([^,]+),\\\"?([^\\\"]+)\\\"?")
    }
}
