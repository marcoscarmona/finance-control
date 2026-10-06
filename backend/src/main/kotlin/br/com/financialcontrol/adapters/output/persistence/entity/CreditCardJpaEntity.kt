package br.com.financialcontrol.adapters.output.persistence.entity

import br.com.financialcontrol.domain.enum.CardDateRule
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
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
    @JdbcTypeCode(SqlTypes.SMALLINT) @Column(name = "closing_day") var closingDay: Int = 1,
    @JdbcTypeCode(SqlTypes.SMALLINT) @Column(name = "due_day") var dueDay: Int = 1,
    @Enumerated(EnumType.STRING) @Column(name = "closing_rule") var closingRule: CardDateRule = CardDateRule.FIXED_DAY,
    @Enumerated(EnumType.STRING) @Column(name = "due_rule") var dueRule: CardDateRule = CardDateRule.FIXED_DAY,
    var active: Boolean = true,
)
