package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CreateCreditCardCommand
import br.com.financialcontrol.application.dto.InvoiceDetails
import br.com.financialcontrol.application.port.input.CreateCreditCardUseCase
import br.com.financialcontrol.application.port.input.GetCardInvoiceUseCase
import br.com.financialcontrol.application.port.input.ListCreditCardsUseCase
import br.com.financialcontrol.application.port.output.BankPersistencePort
import br.com.financialcontrol.application.port.output.CardInvoicePersistencePort
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.application.port.output.ExpenseInstallmentPersistencePort
import br.com.financialcontrol.domain.model.CardInvoice
import br.com.financialcontrol.domain.model.CreditCard
import br.com.financialcontrol.domain.service.InstallmentCalculator
import java.time.YearMonth
import java.util.UUID

class CreditCardApplicationService(
    private val banks: BankPersistencePort,
    private val cards: CreditCardPersistencePort,
    private val invoices: CardInvoicePersistencePort,
    private val installments: ExpenseInstallmentPersistencePort,
    private val calculator: InstallmentCalculator,
) : CreateCreditCardUseCase,
    ListCreditCardsUseCase,
    GetCardInvoiceUseCase {
    override fun execute(
        userId: UUID,
        command: CreateCreditCardCommand,
    ): CreditCard {
        requireOwned(banks.findById(command.bankId)?.userId, userId, "Bank")
        require(command.closingDay in 1..31 && command.dueDay in 1..31) { "closingDay and dueDay must be between 1 and 31" }
        return cards.save(
            CreditCard(
                UUID.randomUUID(),
                userId,
                command.bankId,
                command.name,
                command.lastFourDigits,
                command.limitAmount,
                command.closingDay,
                command.dueDay,
            ),
        )
    }

    override fun execute(userId: UUID): List<CreditCard> = cards.findAllByUserId(userId)

    override fun execute(
        userId: UUID,
        cardId: UUID,
        month: YearMonth,
    ): InvoiceDetails {
        val card = cards.findById(cardId)
        requireOwned(card?.userId, userId, "Credit card")
        val invoice = invoiceFor(card!!, month)
        val rows =
            installments
                .findAllByUserIdAndDueDateBetween(
                    userId,
                    invoice.closingDate.minusMonths(1).plusDays(1),
                    invoice.closingDate,
                ).filter { it.invoiceId == invoice.id }
        return InvoiceDetails(invoice, rows)
    }

    private fun invoiceFor(
        card: CreditCard,
        month: YearMonth,
    ): CardInvoice =
        invoices.findByCardIdAndReferenceMonth(card.id, month) ?: invoices.save(
            CardInvoice(
                UUID.randomUUID(),
                card.id,
                month,
                month.atDay(card.closingDay.coerceAtMost(month.lengthOfMonth())),
                month.atDay(card.dueDay.coerceAtMost(month.lengthOfMonth())).plusMonths(1),
            ),
        )
}
