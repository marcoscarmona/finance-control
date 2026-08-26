package br.com.financialcontrol.adapters.input.web.controller

import br.com.financialcontrol.adapters.input.web.request.CreateExpenseRequest
import br.com.financialcontrol.adapters.input.web.response.ExpenseCreatedResponse
import br.com.financialcontrol.application.dto.CreateExpenseCommand
import br.com.financialcontrol.application.port.input.CreateExpenseUseCase
import br.com.financialcontrol.application.port.input.ListExpensesUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users/{userId}/expenses")
class ExpenseController(
    private val create: CreateExpenseUseCase,
    private val list: ListExpensesUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable userId: UUID,
        @Valid @RequestBody body: CreateExpenseRequest,
    ): ExpenseCreatedResponse {
        val result =
            create.execute(
                userId,
                CreateExpenseCommand(
                    body.categoryId,
                    body.accountId,
                    body.creditCardId,
                    body.subscriptionId,
                    body.description,
                    body.merchant,
                    body.purchaseDate,
                    body.totalAmount,
                    body.paymentMethod,
                    body.installments,
                ),
            )
        return ExpenseCreatedResponse(result.expense, result.installments)
    }

    @GetMapping
    fun list(
        @PathVariable userId: UUID,
    ) = list.execute(userId)
}
