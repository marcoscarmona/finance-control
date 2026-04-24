package br.com.finance.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "subscriptions")
class SubscriptionEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var bank: String? = null,
    @Column(name = "expected_amount", precision = 14, scale = 2)
    var expectedAmount: BigDecimal? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: CategoryEntity? = null,
    var active: Boolean = true,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
