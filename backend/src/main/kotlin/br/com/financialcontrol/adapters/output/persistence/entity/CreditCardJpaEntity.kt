package br.com.financialcontrol.adapters.output.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "credit_cards")
class CreditCardJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id") var userId: UUID = UUID.randomUUID(),
    @Column(name = "bank_id") var bankId: UUID = UUID.randomUUID(),
    var name: String = "",
    @Column(name = "last_four_digits") var lastFourDigits: String = "",
    @Column(name = "limit_amount") var limitAmount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "closing_day") var closingDay: Int = 1,
    @Column(name = "due_day") var dueDay: Int = 1,
    var active: Boolean = true,
)
