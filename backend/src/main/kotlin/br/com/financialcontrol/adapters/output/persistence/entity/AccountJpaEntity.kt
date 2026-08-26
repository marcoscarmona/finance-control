package br.com.financialcontrol.adapters.output.persistence.entity

import br.com.financialcontrol.domain.enum.AccountType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "accounts")
class AccountJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id") var userId: UUID = UUID.randomUUID(),
    @Column(name = "bank_id") var bankId: UUID = UUID.randomUUID(),
    var name: String = "",
    @Enumerated(EnumType.STRING) var type: AccountType = AccountType.CHECKING,
)
