package br.com.financialcontrol.domain.model

import br.com.financialcontrol.domain.enum.BankType
import java.util.UUID

data class Bank(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val code: String?,
    val type: BankType,
)
