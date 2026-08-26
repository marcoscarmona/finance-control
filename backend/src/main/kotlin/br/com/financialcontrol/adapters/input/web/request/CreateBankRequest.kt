package br.com.financialcontrol.adapters.input.web.request

import br.com.financialcontrol.domain.enum.BankType
import jakarta.validation.constraints.NotBlank

data class CreateBankRequest(
    @field:NotBlank val name: String,
    val code: String? = null,
    val type: BankType,
)
