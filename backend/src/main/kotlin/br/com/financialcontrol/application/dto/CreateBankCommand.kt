package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.enum.BankType

data class CreateBankCommand(
    val name: String,
    val code: String?,
    val type: BankType,
)
