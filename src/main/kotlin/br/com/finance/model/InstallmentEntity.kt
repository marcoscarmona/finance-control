package br.com.finance.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "installments")
class InstallmentEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var description: String,
    var bank: String,
    @Column(name = "total_installments")
    var totalInstallments: Int,
    @Column(name = "current_installment")
    var currentInstallment: Int,
    @Column(name = "installment_amount", precision = 14, scale = 2)
    var installmentAmount: BigDecimal,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: CategoryEntity? = null,
    var active: Boolean = true,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
