package br.com.financialcontrol.domain.model

import br.com.financialcontrol.domain.enum.AccountType
import java.util.UUID

data class Account(
    val id: UUID,
    val userId: UUID,
    val bankId: UUID,
    val name: String,
    val type: AccountType,
)
