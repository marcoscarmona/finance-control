package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CreateExpenseCommand
import br.com.financialcontrol.application.port.output.AccountPersistencePort
import br.com.financialcontrol.application.port.output.CardInvoicePersistencePort
import br.com.financialcontrol.application.port.output.CategoryPersistencePort
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.application.port.output.ExpenseInstallmentPersistencePort
import br.com.financialcontrol.application.port.output.ExpensePersistencePort
import br.com.financialcontrol.domain.enum.PaymentMethod
import br.com.financialcontrol.domain.model.Account
import br.com.financialcontrol.domain.model.CardInvoice
import br.com.financialcontrol.domain.model.Category
import br.com.financialcontrol.domain.model.CreditCard
import br.com.financialcontrol.domain.model.Expense
import br.com.financialcontrol.domain.model.ExpenseInstallment
import br.com.financialcontrol.domain.service.InstallmentCalculator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

class ExpenseApplicationServiceTest {
    @Test
    fun `creates installments with correct rounding and next invoice on closing day`() {
        val userId = UUID.randomUUID()
        val category = Category(UUID.randomUUID(), userId, "Food", "#000000")
        val card = CreditCard(UUID.randomUUID(), userId, UUID.randomUUID(), "Card", "1234", BigDecimal("1000"), 25, 5)
        val service =
            ExpenseApplicationService(
                Categories(category),
                Accounts(),
                Cards(card),
                Expenses(),
                Installments(),
                Invoices(),
                InstallmentCalculator(),
            )
        val result =
            service.execute(
                userId,
                CreateExpenseCommand(
                    category.id,
                    null,
                    card.id,
                    null,
                    "Market",
                    null,
                    LocalDate.of(2026, 5, 25),
                    BigDecimal("100.00"),
                    PaymentMethod.CREDIT_CARD,
                    3,
                ),
            )
        assertEquals(
            listOf(BigDecimal("33.33"), BigDecimal("33.33"), BigDecimal("33.34")),
            result.installments.map { it.amount },
        )
        assertEquals(YearMonth.of(2026, 6), Invoices.values[result.installments.first().invoiceId]!!.referenceMonth)
    }

    private class Categories(
        private val category: Category,
    ) : CategoryPersistencePort {
        override fun save(category: Category) = category

        override fun findById(id: UUID) = category.takeIf { it.id == id }

        override fun findAllByUserId(userId: UUID) = listOf(category).filter { it.userId == userId }
    }

    private class Accounts : AccountPersistencePort {
        override fun save(account: Account) = account

        override fun findById(id: UUID): Account? = null

        override fun findAllByUserId(userId: UUID) = emptyList<Account>()
    }

    private class Cards(
        private val card: CreditCard,
    ) : CreditCardPersistencePort {
        override fun save(card: CreditCard) = card

        override fun findById(id: UUID) = card.takeIf { it.id == id }

        override fun findAllByUserId(userId: UUID) = listOf(card).filter { it.userId == userId }
    }

    private class Expenses : ExpensePersistencePort {
        override fun save(expense: Expense) = expense

        override fun findById(id: UUID): Expense? = null

        override fun findAllByUserId(userId: UUID) = emptyList<Expense>()
    }

    private class Installments : ExpenseInstallmentPersistencePort {
        override fun saveAll(items: List<ExpenseInstallment>) = Unit

        override fun findAllByExpenseId(expenseId: UUID) = emptyList<ExpenseInstallment>()

        override fun findAllByUserIdAndDueDateBetween(
            userId: UUID,
            start: LocalDate,
            end: LocalDate,
        ) = emptyList<ExpenseInstallment>()
    }

    private class Invoices : CardInvoicePersistencePort {
        companion object {
            val values = mutableMapOf<UUID, CardInvoice>()
        }

        override fun save(invoice: CardInvoice) = invoice.also { values[it.id] = it }

        override fun findByCardIdAndReferenceMonth(
            cardId: UUID,
            month: YearMonth,
        ): CardInvoice? = null
    }
}
