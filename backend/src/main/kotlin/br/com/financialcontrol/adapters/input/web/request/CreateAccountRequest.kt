package br.com.financialcontrol.adapters.input.web.request
import br.com.financialcontrol.domain.enum.AccountType
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class CreateAccountRequest(
    val bankId: UUID,
    @field:NotBlank val name: String,
    val type: AccountType,
)
