package br.com.finance.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "merchant_rules")
class MerchantRuleEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var keyword: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: CategoryEntity,

    @Column(nullable = false)
    var priority: Int = 100,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
