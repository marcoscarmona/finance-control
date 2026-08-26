package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.enum.AccountType
import java.util.UUID

data class CreateAccountCommand(
    val bankId: UUID,
    val name: String,
    val type: AccountType,
)
