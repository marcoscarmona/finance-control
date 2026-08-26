package br.com.financialcontrol.application.port.input

import br.com.financialcontrol.application.dto.CreateCreditCardCommand
import br.com.financialcontrol.application.dto.InvoiceDetails
import br.com.financialcontrol.domain.model.CreditCard
import java.time.YearMonth
import java.util.UUID

interface CreateCreditCardUseCase {
    fun execute(
        userId: UUID,
        command: CreateCreditCardCommand,
    ): CreditCard
}

interface ListCreditCardsUseCase {
    fun execute(userId: UUID): List<CreditCard>
}

interface GetCardInvoiceUseCase {
    fun execute(
        userId: UUID,
        cardId: UUID,
        month: YearMonth,
    ): InvoiceDetails
}
