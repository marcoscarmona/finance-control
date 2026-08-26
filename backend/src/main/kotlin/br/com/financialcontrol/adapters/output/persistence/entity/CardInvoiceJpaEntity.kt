package br.com.financialcontrol.adapters.output.persistence.entity

import br.com.financialcontrol.domain.enum.InvoiceStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(
    name = "card_invoices",
    uniqueConstraints = [UniqueConstraint(columnNames = ["credit_card_id", "reference_month"])],
)
class CardInvoiceJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "credit_card_id") var creditCardId: UUID = UUID.randomUUID(),
    @Column(name = "reference_month") var referenceMonth: String = "",
    @Column(name = "closing_date") var closingDate: LocalDate = LocalDate.now(),
    @Column(name = "due_date") var dueDate: LocalDate = LocalDate.now(),
    @Enumerated(EnumType.STRING) var status: InvoiceStatus = InvoiceStatus.OPEN,
)
