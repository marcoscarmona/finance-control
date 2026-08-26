package br.com.financialcontrol.adapters.input.web.controller

import br.com.financialcontrol.adapters.input.web.request.CreateSubscriptionRequest
import br.com.financialcontrol.application.dto.CreateSubscriptionCommand
import br.com.financialcontrol.application.port.input.CreateSubscriptionUseCase
import br.com.financialcontrol.application.port.input.ListSubscriptionsUseCase
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
@RequestMapping("/api/users/{userId}/subscriptions")
class SubscriptionController(
    private val create: CreateSubscriptionUseCase,
    private val list: ListSubscriptionsUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable userId: UUID,
        @Valid @RequestBody body: CreateSubscriptionRequest,
    ) = create.execute(
        userId,
        CreateSubscriptionCommand(
            body.categoryId,
            body.creditCardId,
            body.accountId,
            body.name,
            body.amount,
            body.frequency,
            body.chargeDay,
        ),
    )

    @GetMapping
    fun list(
        @PathVariable userId: UUID,
    ) = list.execute(userId)
}
