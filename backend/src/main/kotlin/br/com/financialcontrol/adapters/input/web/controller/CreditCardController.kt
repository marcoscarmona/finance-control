package br.com.financialcontrol.adapters.input.web.controller

import br.com.financialcontrol.adapters.input.web.request.CreateCreditCardRequest
import br.com.financialcontrol.application.dto.CreateCreditCardCommand
import br.com.financialcontrol.application.port.input.CreateCreditCardUseCase
import br.com.financialcontrol.application.port.input.GetCardInvoiceUseCase
import br.com.financialcontrol.application.port.input.ListCreditCardsUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.YearMonth
import java.util.UUID

@RestController
@RequestMapping("/api/users/{userId}/credit-cards")
class CreditCardController(
    private val create: CreateCreditCardUseCase,
    private val list: ListCreditCardsUseCase,
    private val invoice: GetCardInvoiceUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable userId: UUID,
        @Valid @RequestBody body: CreateCreditCardRequest,
    ) = create.execute(
        userId,
        CreateCreditCardCommand(
            body.bankId,
            body.name,
            body.lastFourDigits,
            body.limitAmount,
            body.closingDay,
            body.dueDay,
            body.closingRule,
            body.dueRule,
        ),
    )

    @GetMapping
    fun list(
        @PathVariable userId: UUID,
    ) = list.execute(userId)

    @GetMapping("/{cardId}/invoices/{year}/{month}")
    fun invoice(
        @PathVariable userId: UUID,
        @PathVariable cardId: UUID,
        @PathVariable year: Int,
        @PathVariable month: Int,
    ) = invoice.execute(userId, cardId, YearMonth.of(year, month))
}
