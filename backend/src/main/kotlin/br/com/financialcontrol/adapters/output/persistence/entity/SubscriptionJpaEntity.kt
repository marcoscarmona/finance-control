package br.com.financialcontrol.adapters.output.persistence.entity

import br.com.financialcontrol.domain.enum.SubscriptionFrequency
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
@Table(name = "subscriptions")
class SubscriptionJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id") var userId: UUID = UUID.randomUUID(),
    @Column(name = "category_id") var categoryId: UUID = UUID.randomUUID(),
    @Column(name = "credit_card_id") var creditCardId: UUID? = null,
    @Column(name = "account_id") var accountId: UUID? = null,
    var name: String = "",
    var amount: BigDecimal = BigDecimal.ZERO,
    @Enumerated(EnumType.STRING) var frequency: SubscriptionFrequency = SubscriptionFrequency.MONTHLY,
    @JdbcTypeCode(SqlTypes.SMALLINT) @Column(name = "charge_day") var chargeDay: Int = 1,
    var active: Boolean = true,
)
