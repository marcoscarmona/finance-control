package br.com.financialcontrol.adapters.input.web.controller
import br.com.financialcontrol.adapters.input.web.request.CreateCategoryRequest
import br.com.financialcontrol.application.dto.CreateCategoryCommand
import br.com.financialcontrol.application.port.input.CreateCategoryUseCase
import br.com.financialcontrol.application.port.input.ListCategoriesUseCase
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
@RequestMapping("/api/users/{userId}/categories")
class CategoryController(
    private val create: CreateCategoryUseCase,
    private val list: ListCategoriesUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable userId: UUID,
        @Valid @RequestBody body: CreateCategoryRequest,
    ) = create.execute(userId, CreateCategoryCommand(body.name, body.color))

    @GetMapping fun list(
        @PathVariable userId: UUID,
    ) = list.execute(userId)
}
