package br.com.financialcontrol.adapters.output.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "card_invoice_manual_totals",
    uniqueConstraints = [UniqueConstraint(columnNames = ["credit_card_id", "reference_month"])],
)
class CardInvoiceManualTotalJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "credit_card_id") var creditCardId: UUID = UUID.randomUUID(),
    @Column(name = "reference_month") var referenceMonth: String = "",
    @Column(name = "total_amount") var totalAmount: BigDecimal = BigDecimal.ZERO,
    @Column(name = "updated_at") var updatedAt: LocalDateTime = LocalDateTime.now(),
)
