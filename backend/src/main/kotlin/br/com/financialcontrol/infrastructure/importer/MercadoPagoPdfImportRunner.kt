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
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
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
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

@Component
@ConditionalOnProperty(name = ["finance.mercado-pago.pdf-file"])
class MercadoPagoPdfImportRunner(
    @Value("\${finance.mercado-pago.pdf-file}") private val sourceFile: String,
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
        val file = File(sourceFile)
        require(file.isFile) { "Mercado Pago PDF not found: ${file.absolutePath}" }
        val user = requireNotNull(users.findByEmail(email.trim().lowercase())) { "User not found for $email" }
        val statement = MercadoPagoStatementParser.parse(file)
        resetMercadoPagoData(user.id)
        val bank = createBank.execute(user.id, CreateBankCommand("Mercado Pago", null, BankType.DIGITAL_WALLET))
        val card =
            createCard.execute(
                user.id,
                CreateCreditCardCommand(
                    bank.id,
                    "Mercado Pago",
                    statement.lastFourDigits,
                    BigDecimal("35000.00"),
                    5,
                    13,
                    CardDateRule.FIXED_DAY,
                    CardDateRule.FIXED_DAY,
                ),
            )
        val categories = categoryMap(user.id)
        val imported = mutableSetOf<String>()
        statement.rows.forEach { row ->
            val merchant = row.merchant.trim()
            val key = "${merchant.key()}|${row.amount}|${row.installments}"
            if (!imported.add(key)) return@forEach
            val firstInvoice = statement.month.minusMonths((row.installmentNumber - 1).toLong())
            val purchaseDate = purchaseDate(firstInvoice, row.day, row.month)
            val category = categories[categoryName(merchant)] ?: categories.getValue("Outros")
            createExpense.execute(
                user.id,
                CreateExpenseCommand(
                    category,
                    null,
                    card.id,
                    null,
                    merchant,
                    merchant,
                    purchaseDate,
                    row.amount.multiply(BigDecimal(row.installments)),
                    PaymentMethod.CREDIT_CARD,
                    row.installments,
                ),
            )
        }
        jdbc.update(
            "INSERT INTO card_invoice_manual_totals (id, credit_card_id, reference_month, total_amount, updated_at) VALUES (?::uuid, ?::uuid, ?, ?, CURRENT_TIMESTAMP)",
            UUID.randomUUID().toString(),
            card.id.toString(),
            statement.month.toString(),
            statement.closedTotal,
        )
        println("Mercado Pago import completed: ${imported.size} purchases")
    }

    private fun resetMercadoPagoData(userId: UUID) {
        val cardIds =
            jdbc.queryForList(
                "SELECT c.id FROM credit_cards c JOIN banks b ON b.id = c.bank_id WHERE c.user_id = ?::uuid AND b.name = 'Mercado Pago' AND c.last_four_digits = '8866'",
                String::class.java,
                userId.toString(),
            )
        cardIds.forEach { cardId ->
            jdbc.update("DELETE FROM card_invoice_manual_totals WHERE credit_card_id = ?::uuid", cardId)
            jdbc.update(
                "DELETE FROM expense_installments WHERE expense_id IN (SELECT id FROM expenses WHERE credit_card_id = ?::uuid)",
                cardId,
            )
            jdbc.update("DELETE FROM card_invoices WHERE credit_card_id = ?::uuid", cardId)
            jdbc.update("DELETE FROM expenses WHERE credit_card_id = ?::uuid", cardId)
            jdbc.update("DELETE FROM credit_cards WHERE id = ?::uuid", cardId)
        }
        jdbc.update("DELETE FROM banks WHERE user_id = ?::uuid AND name = 'Mercado Pago'", userId.toString())
    }

    private fun categoryMap(userId: UUID): Map<String, UUID> =
        CATEGORY_SEEDS.associate { seed ->
            seed.name to (
                jdbc
                    .queryForList(
                        "SELECT id FROM categories WHERE user_id = ?::uuid AND name = ?",
                        UUID::class.java,
                        userId.toString(),
                        seed.name,
                    ).firstOrNull() ?: createCategory.execute(userId, CreateCategoryCommand(seed.name, seed.color)).id
            )
        }

    private fun purchaseDate(
        firstInvoice: YearMonth,
        day: Int,
        month: Int,
    ): LocalDate {
        var date = LocalDate.of(firstInvoice.year, month, day)
        if (date.monthValue != firstInvoice.monthValue && date.isAfter(firstInvoice.atDay(5))) date = date.minusYears(1)
        if (date.monthValue == firstInvoice.monthValue && date.dayOfMonth >= 5) date = date.minusMonths(1)
        return date
    }

    private fun categoryName(merchant: String): String =
        when {
            merchant.key().containsAny("raia", "farma", "drogasil") -> "Farmácia"
            merchant.key().containsAny("ifd", "delivery", "food service") -> "Delivery"
            merchant.key().containsAny("mercadolivre", "mercado", "super", "hortifruti", "emporio", "praca") -> "Mercado"
            else -> "Outros"
        }

    private fun String.key() =
        Normalizer
            .normalize(this, Normalizer.Form.NFD)
            .replace("\\p{M}".toRegex(), "")
            .lowercase()
            .trim()

    private fun String.containsAny(vararg values: String) = values.any(::contains)

    private data class CategorySeed(
        val name: String,
        val color: String,
    )

    private companion object {
        val CATEGORY_SEEDS =
            listOf(
                CategorySeed("Outros", "#64748B"),
                CategorySeed("Alimentação", "#EAB308"),
                CategorySeed("Farmácia", "#EC4899"),
                CategorySeed("Delivery", "#F97316"),
                CategorySeed("Mercado", "#22C55E"),
            )
    }
}

internal data class MercadoPagoStatement(
    val month: YearMonth,
    val lastFourDigits: String,
    val closedTotal: BigDecimal,
    val rows: List<MercadoPagoInstallmentRow>,
)

internal data class MercadoPagoInstallmentRow(
    val day: Int,
    val month: Int,
    val merchant: String,
    val installmentNumber: Int,
    val installments: Int,
    val amount: BigDecimal,
)

internal object MercadoPagoStatementParser {
    fun parse(file: File): MercadoPagoStatement = parseText(Loader.loadPDF(file).use { PDFTextStripper().getText(it) })

    fun parseText(text: String): MercadoPagoStatement {
        val issueYear =
            Regex("Emitida em:\\s*\\d{2}/\\d{2}/(20\\d{2})")
                .find(text)
                ?.groupValues
                ?.get(1)
                ?.toInt()
        val statementMonth = Regex("fatura de ([a-zç]+)", RegexOption.IGNORE_CASE).find(text)?.groupValues?.get(1)
        val month =
            YearMonth.of(
                issueYear ?: error("Statement issue year not found"),
                statementMonth?.let(::monthNumber) ?: error("Mercado Pago statement month not found"),
            )
        val lastFour = Regex("Cartão Visa \\[\\*+([0-9]{4})\\]").find(text)?.groupValues?.get(1) ?: error("Card ending not found")
        val closedTotal =
            Regex("Total a pagar\\s*R\\$\\s*([0-9.]+,[0-9]{2})", RegexOption.DOT_MATCHES_ALL)
                .find(text)
                ?.groupValues
                ?.get(1)
                ?.money()
                ?: error("Closed total not found")
        val rows =
            INSTALLMENT
                .findAll(text)
                .map { match ->
                    MercadoPagoInstallmentRow(
                        match.groupValues[1].toInt(),
                        match.groupValues[2].toInt(),
                        match.groupValues[3].trim(),
                        match.groupValues[4].toInt(),
                        match.groupValues[5].toInt(),
                        match.groupValues[6].money(),
                    )
                }.toList()
        require(rows.isNotEmpty()) { "No Mercado Pago installment purchases found" }
        return MercadoPagoStatement(month, lastFour, closedTotal, rows)
    }

    private fun monthNumber(month: String) =
        mapOf(
            "janeiro" to 1,
            "fevereiro" to 2,
            "março" to 3,
            "abril" to 4,
            "maio" to 5,
            "junho" to 6,
            "julho" to 7,
            "agosto" to 8,
            "setembro" to 9,
            "outubro" to 10,
            "novembro" to 11,
            "dezembro" to 12,
        ).getValue(month.lowercase())

    private fun String.money() = replace(".", "").replace(',', '.').toBigDecimal().setScale(2, RoundingMode.HALF_UP)

    private val INSTALLMENT =
        Regex("(?m)^(\\d{2})/(\\d{2})\\s+(.+?)\\s+Parcela\\s+(\\d+)\\s+de\\s+(\\d+)\\s+R\\$\\s*([0-9.]+,[0-9]{2})$")
}
