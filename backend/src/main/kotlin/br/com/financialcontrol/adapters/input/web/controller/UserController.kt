package br.com.financialcontrol.adapters.input.web.controller

import br.com.financialcontrol.adapters.input.web.request.CreateUserRequest
import br.com.financialcontrol.application.dto.CreateUserCommand
import br.com.financialcontrol.application.port.input.CreateUserUseCase
import br.com.financialcontrol.application.port.input.FindUserByEmailUseCase
import br.com.financialcontrol.application.port.input.GetUserUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val createUser: CreateUserUseCase,
    private val getUser: GetUserUseCase,
    private val findUserByEmail: FindUserByEmailUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody body: CreateUserRequest,
    ) = createUser.execute(CreateUserCommand(body.name, body.email))

    @GetMapping("/{userId}")
    fun get(
        @PathVariable userId: UUID,
    ) = getUser.execute(userId)

    @GetMapping("/by-email")
    fun byEmail(
        @RequestParam email: String,
    ) = findUserByEmail.execute(email)
}
