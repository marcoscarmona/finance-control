package br.com.financialcontrol.adapters.input.web.controller

import br.com.financialcontrol.adapters.input.web.request.CreateBankRequest
import br.com.financialcontrol.application.dto.CreateBankCommand
import br.com.financialcontrol.application.port.input.CreateBankUseCase
import br.com.financialcontrol.application.port.input.ListBanksUseCase
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
@RequestMapping("/api/users/{userId}/banks")
class BankController(
    private val create: CreateBankUseCase,
    private val list: ListBanksUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable userId: UUID,
        @Valid @RequestBody body: CreateBankRequest,
    ) = create.execute(userId, CreateBankCommand(body.name, body.code, body.type))

    @GetMapping
    fun list(
        @PathVariable userId: UUID,
    ) = list.execute(userId)
}
