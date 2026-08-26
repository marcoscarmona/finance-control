package br.com.financialcontrol.adapters.input.web.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateCategoryRequest(
    @field:NotBlank val name: String,
    @field:Pattern(regexp = "^#[0-9A-Fa-f]{6}$") val color: String = "#888888",
)
