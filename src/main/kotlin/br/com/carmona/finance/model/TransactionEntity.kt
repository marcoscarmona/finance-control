package br.com.carmona.finance.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Entity
@Table(name = "transactions")
class TransactionEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "transaction_date", nullable = false)
    var transactionDate: LocalDate,

    @Column(name = "month_ref", nullable = false)
    var monthRef: String = transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM")),

    @Column(nullable = false)
    var bank: String,

    var card: String? = null,

    @Column(nullable = false)
    var description: String,

    var merchant: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: CategoryEntity? = null,

    @Column(nullable = false, precision = 14, scale = 2)
    var amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: TransactionType,

    @Column(name = "installment_number")
    var installmentNumber: Int? = null,

    @Column(name = "installment_total")
    var installmentTotal: Int? = null,

    var source: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
