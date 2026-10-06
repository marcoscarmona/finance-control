package br.com.financialcontrol.infrastructure.importer

import br.com.financialcontrol.application.dto.CreateAccountCommand
import br.com.financialcontrol.application.dto.CreateBankCommand
import br.com.financialcontrol.application.dto.CreateCategoryCommand
import br.com.financialcontrol.application.dto.CreateCreditCardCommand
import br.com.financialcontrol.application.dto.CreateExpenseCommand
import br.com.financialcontrol.application.port.input.CreateAccountUseCase
import br.com.financialcontrol.application.port.input.CreateBankUseCase
import br.com.financialcontrol.application.port.input.CreateCategoryUseCase
import br.com.financialcontrol.application.port.input.CreateCreditCardUseCase
import br.com.financialcontrol.application.port.input.CreateExpenseUseCase
import br.com.financialcontrol.application.port.input.ListAccountsUseCase
import br.com.financialcontrol.application.port.input.ListBanksUseCase
import br.com.financialcontrol.application.port.input.ListCategoriesUseCase
import br.com.financialcontrol.application.port.input.ListCreditCardsUseCase
import br.com.financialcontrol.application.port.output.ExpensePersistencePort
import br.com.financialcontrol.application.port.output.UserPersistencePort
import br.com.financialcontrol.domain.enum.AccountType
import br.com.financialcontrol.domain.enum.BankType
import br.com.financialcontrol.domain.enum.CardDateRule
import br.com.financialcontrol.domain.enum.PaymentMethod
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.Normalizer
import java.time.YearMonth
import java.util.Locale

@Component
@ConditionalOnProperty(name = ["finance.import.file"])
class SpreadsheetImportRunner(
    @Value("\${finance.import.file}") private val spreadsheetPath: String,
    @Value("\${finance.import.email:}") private val email: String,
    private val users: UserPersistencePort,
    private val expenses: ExpensePersistencePort,
    private val createBank: CreateBankUseCase,
    private val listBanks: ListBanksUseCase,
    private val createAccount: CreateAccountUseCase,
    private val listAccounts: ListAccountsUseCase,
    private val createCategory: CreateCategoryUseCase,
    private val listCategories: ListCategoriesUseCase,
    private val createCard: CreateCreditCardUseCase,
    private val listCards: ListCreditCardsUseCase,
    private val createExpense: CreateExpenseUseCase,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        require(email.isNotBlank()) { "finance.import.email is required" }
        val file = File(spreadsheetPath)
        require(file.isFile) { "Spreadsheet not found: ${file.absolutePath}" }
        val user = requireNotNull(users.findByEmail(email.trim().lowercase())) { "User not found for $email" }
        val report = ImportReport()
        val banks = listBanks.execute(user.id).associateBy { it.name.key() }.toMutableMap()

        fun bank(
            name: String,
            type: BankType,
        ): java.util.UUID =
            banks
                .getOrPut(name.key()) {
                    report.created += "bank:$name"
                    createBank.execute(user.id, CreateBankCommand(name, null, type))
                }.id

        val nubankBankId = bank("Nubank", BankType.BANK)
        val mercadoPagoBankId = bank("Mercado Pago", BankType.DIGITAL_WALLET)
        val accounts = listAccounts.execute(user.id).associateBy { it.name.key() }.toMutableMap()
        val nubankAccount =
            accounts.getOrPut("Conta Nubank".key()) {
                report.created += "account:Conta Nubank"
                createAccount.execute(user.id, CreateAccountCommand(nubankBankId, "Conta Nubank", AccountType.CHECKING))
            }
        val cards = listCards.execute(user.id).associateBy { it.name.key() }.toMutableMap()

        fun card(
            name: String,
            command: CreateCreditCardCommand,
        ) {
            if (cards[name.key()] == null) {
                cards[name.key()] = createCard.execute(user.id, command)
                report.created += "card:$name"
            }
        }
        card(
            "Nubank",
            CreateCreditCardCommand(
                nubankBankId,
                "Nubank",
                "9526",
                BigDecimal("40000.00"),
                26,
                5,
                CardDateRule.FIXED_DAY,
                CardDateRule.NTH_BUSINESS_DAY_AFTER_CLOSING,
            ),
        )
        card(
            "Mercado Pago",
            CreateCreditCardCommand(
                mercadoPagoBankId,
                "Mercado Pago",
                "9526",
                BigDecimal("35000.00"),
                5,
                13,
                CardDateRule.NTH_BUSINESS_DAY,
                CardDateRule.NEXT_BUSINESS_DAY_ON_OR_AFTER,
            ),
        )
        val categories = listCategories.execute(user.id).associateBy { it.name.key() }.toMutableMap()

        fun category(description: String) =
            categoryFor(description).let { suggestion ->
                categories.getOrPut(suggestion.name.key()) {
                    report.created += "category:${suggestion.name}"
                    createCategory.execute(user.id, CreateCategoryCommand(suggestion.name, suggestion.color))
                }
            }

        WorkbookFactory.create(file).use { workbook ->
            workbook.forEach { sheet ->
                val month = monthFromSheetName(sheet.sheetName) ?: return@forEach
                sheet.forEach { row ->
                    listOf(0 to 1, 3 to 4).forEach { (descriptionColumn, valueColumn) ->
                        val description =
                            row
                                .getCell(descriptionColumn)
                                ?.stringValue()
                                ?.trim()
                                .orEmpty()
                        val value = row.getCell(valueColumn)?.moneyValue()
                        if (description.isBlank() && value == null) return@forEach
                        if (description.isBlank() || value == null) {
                            report.ignored += "${sheet.sheetName}:${row.rowNum + 1} incomplete"
                            return@forEach
                        }
                        if (description.isIgnored()) return@forEach
                        val date = month.atDay(1)
                        if (expenses.existsByUserIdAndDescriptionAndPurchaseDateAndTotalAmount(user.id, description, date, value)) {
                            report.duplicates += "${sheet.sheetName}:${row.rowNum + 1} $description"
                            return@forEach
                        }
                        createExpense.execute(
                            user.id,
                            CreateExpenseCommand(
                                category(description).id,
                                nubankAccount.id,
                                null,
                                null,
                                description,
                                null,
                                date,
                                value,
                                PaymentMethod.PIX,
                                1,
                            ),
                        )
                        report.imported += "${sheet.sheetName}:${row.rowNum + 1} $description"
                    }
                }
            }
        }
        println(report.render())
    }

    private fun Cell.stringValue() = DataFormatter(Locale("pt", "BR")).formatCellValue(this)

    private fun Cell.moneyValue(): BigDecimal? =
        when (cellType) {
            org.apache.poi.ss.usermodel.CellType.NUMERIC,
            org.apache.poi.ss.usermodel.CellType.FORMULA,
            ->
                runCatching {
                    BigDecimal(numericCellValue.toString()).setScale(2, RoundingMode.HALF_UP)
                }.getOrNull()
            org.apache.poi.ss.usermodel.CellType.STRING ->
                stringCellValue
                    .replace("R$", "")
                    .replace(".", "")
                    .replace(',', '.')
                    .trim()
                    .toBigDecimalOrNull()
                    ?.setScale(2, RoundingMode.HALF_UP)
            else -> null
        }?.takeIf { it > BigDecimal.ZERO }

    private fun monthFromSheetName(name: String): YearMonth? {
        val normalized = name.normalized()
        val match =
            Regex(
                "(janeiro|fevereiro|marco|abril|maio|junho|julho|agosto|setembro|outubro|novembro|dezembro)\\s*-?\\s*(20\\d{2})",
            ).find(normalized)
                ?: return null
        val number =
            listOf(
                "janeiro",
                "fevereiro",
                "marco",
                "abril",
                "maio",
                "junho",
                "julho",
                "agosto",
                "setembro",
                "outubro",
                "novembro",
                "dezembro",
            ).indexOf(match.groupValues[1]) +
                1
        return YearMonth.of(match.groupValues[2].toInt(), number)
    }

    private fun String.isIgnored(): Boolean = normalized() in setOf("cartoes", "mercado pago", "dividas", "total", "salario", "resultado s")

    private fun String.key() = normalized()

    private fun String.normalized() =
        Normalizer
            .normalize(this, Normalizer.Form.NFD)
            .replace("\\p{M}".toRegex(), "")
            .lowercase()
            .trim()

    private fun categoryFor(description: String): CategorySuggestion {
        val normalized = description.normalized()
        return when {
            normalized.containsAny("vivo", "internet", "luz", "agua", "casa", "aluguel") -> CategorySuggestion("Moradia", "#2563EB")
            normalized.containsAny("academia", "cabelo", "medico", "farmacia") -> CategorySuggestion("Saúde e bem-estar", "#16A34A")
            normalized.containsAny("viagem", "uber", "combustivel", "carro") -> CategorySuggestion("Transporte", "#F97316")
            normalized.containsAny("chatgpt", "netflix", "spotify", "assinatura") -> CategorySuggestion("Assinaturas", "#7C3AED")
            normalized.containsAny("mae", "molly", "balanca") -> CategorySuggestion("Família", "#DB2777")
            else -> CategorySuggestion("Outros", "#64748B")
        }
    }

    private fun String.containsAny(vararg words: String) = words.any(::contains)

    private data class CategorySuggestion(
        val name: String,
        val color: String,
    )

    private class ImportReport {
        val created = mutableListOf<String>()
        val imported = mutableListOf<String>()
        val duplicates = mutableListOf<String>()
        val ignored = mutableListOf<String>()

        fun render() =
            """
            Finance Control import completed
            Created: ${created.size}; imported: ${imported.size}; duplicates: ${duplicates.size}; ignored: ${ignored.size}
            Created items: ${created.joinToString()}
            Imported items: ${imported.joinToString()}
            Duplicates: ${duplicates.joinToString()}
            Ignored: ${ignored.joinToString()}
            """.trimIndent()
    }
}
