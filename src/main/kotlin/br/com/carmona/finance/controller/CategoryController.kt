package br.com.carmona.finance.controller

import br.com.carmona.finance.dto.CategoryRequest
import br.com.carmona.finance.dto.CategoryResponse
import br.com.carmona.finance.model.CategoryEntity
import br.com.carmona.finance.repository.CategoryRepository
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryRepository: CategoryRepository
) {
    @GetMapping
    fun list() = categoryRepository.findAll().map { it.toResponse() }

    @PostMapping
    fun create(@RequestBody @Valid request: CategoryRequest): CategoryResponse = categoryRepository.save(
        CategoryEntity(name = request.name.trim(), monthlyLimit = request.monthlyLimit)
    ).toResponse()

    private fun CategoryEntity.toResponse() = CategoryResponse(id, name, monthlyLimit)
}
