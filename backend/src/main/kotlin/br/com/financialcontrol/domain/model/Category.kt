package br.com.financialcontrol.domain.model

import java.util.UUID

data class Category(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val color: String,
    val active: Boolean = true,
)
