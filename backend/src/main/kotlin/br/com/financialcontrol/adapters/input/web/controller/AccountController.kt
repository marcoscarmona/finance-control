package br.com.financialcontrol.adapters.input.web.controller

import br.com.financialcontrol.adapters.input.web.request.CreateAccountRequest
import br.com.financialcontrol.application.dto.CreateAccountCommand
import br.com.financialcontrol.application.port.input.CreateAccountUseCase
import br.com.financialcontrol.application.port.input.ListAccountsUseCase
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
@RequestMapping("/api/users/{userId}/accounts")
class AccountController(
    private val create: CreateAccountUseCase,
    private val list: ListAccountsUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable userId: UUID,
        @Valid @RequestBody body: CreateAccountRequest,
    ) = create.execute(userId, CreateAccountCommand(body.bankId, body.name, body.type))

    @GetMapping
    fun list(
        @PathVariable userId: UUID,
    ) = list.execute(userId)
}
