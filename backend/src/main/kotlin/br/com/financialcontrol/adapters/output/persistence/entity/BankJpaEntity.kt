package br.com.financialcontrol.adapters.output.persistence.entity

import br.com.financialcontrol.domain.enum.BankType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "banks")
class BankJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id") var userId: UUID = UUID.randomUUID(),
    var name: String = "",
    var code: String? = null,
    @Enumerated(EnumType.STRING) var type: BankType = BankType.BANK,
)
