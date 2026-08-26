package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CreateExpenseCommand
import br.com.financialcontrol.application.dto.ExpenseCreationResult
import br.com.financialcontrol.application.port.input.CreateExpenseUseCase
import br.com.financialcontrol.application.port.input.ListExpensesUseCase
import br.com.financialcontrol.application.port.output.AccountPersistencePort
import br.com.financialcontrol.application.port.output.CardInvoicePersistencePort
import br.com.financialcontrol.application.port.output.CategoryPersistencePort
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.application.port.output.ExpenseInstallmentPersistencePort
import br.com.financialcontrol.application.port.output.ExpensePersistencePort
import br.com.financialcontrol.domain.enum.PaymentMethod
import br.com.financialcontrol.domain.model.CardInvoice
import br.com.financialcontrol.domain.model.Expense
import br.com.financialcontrol.domain.model.ExpenseInstallment
import br.com.financialcontrol.domain.service.InstallmentCalculator
import java.time.LocalDateTime
import java.util.UUID

class ExpenseApplicationService(
    private val categories: CategoryPersistencePort,
    private val accounts: AccountPersistencePort,
    private val cards: CreditCardPersistencePort,
    private val expenses: ExpensePersistencePort,
    private val installments: ExpenseInstallmentPersistencePort,
    private val invoices: CardInvoicePersistencePort,
    private val calculator: InstallmentCalculator,
) : CreateExpenseUseCase,
    ListExpensesUseCase {
    override fun execute(
        userId: UUID,
        command: CreateExpenseCommand,
    ): ExpenseCreationResult {
        requireOwned(categories.findById(command.categoryId)?.userId, userId, "Category")
        command.accountId?.let { requireOwned(accounts.findById(it)?.userId, userId, "Account") }
        command.creditCardId?.let { requireOwned(cards.findById(it)?.userId, userId, "Credit card") }
        require((command.paymentMethod == PaymentMethod.CREDIT_CARD) == (command.creditCardId != null)) {
            "creditCardId is required only for CREDIT_CARD"
        }
        val expense =
            expenses.save(
                Expense(
                    UUID.randomUUID(),
                    userId,
                    command.categoryId,
                    command.accountId,
                    command.creditCardId,
                    command.subscriptionId,
                    command.description,
                    command.merchant,
                    command.purchaseDate,
                    command.totalAmount,
                    command.paymentMethod,
                    createdAt = LocalDateTime.now(),
                ),
            )
        val values = calculator.split(command.totalAmount, command.installments)
        val rows =
            values.mapIndexed { index, amount ->
                val date = command.purchaseDate.plusMonths(index.toLong())
                val invoice = command.creditCardId?.let { invoiceFor(cards.findById(it)!!, date) }
                ExpenseInstallment(
                    UUID.randomUUID(),
                    expense.id,
                    invoice?.id,
                    index + 1,
                    values.size,
                    amount,
                    invoice?.dueDate ?: date,
                )
            }
        installments.saveAll(rows)
        return ExpenseCreationResult(expense, rows)
    }

    override fun execute(userId: UUID): List<Expense> = expenses.findAllByUserId(userId)

    private fun invoiceFor(
        card: br.com.financialcontrol.domain.model.CreditCard,
        date: java.time.LocalDate,
    ): CardInvoice {
        val month = calculator.referenceMonth(date, card.closingDay)
        return invoices.findByCardIdAndReferenceMonth(card.id, month) ?: invoices.save(
            CardInvoice(
                UUID.randomUUID(),
                card.id,
                month,
                month.atDay(card.closingDay.coerceAtMost(month.lengthOfMonth())),
                month.atDay(card.dueDay.coerceAtMost(month.lengthOfMonth())).plusMonths(1),
            ),
        )
    }
}
