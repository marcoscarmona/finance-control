package br.com.financialcontrol.adapters.output.persistence.entity

import br.com.financialcontrol.domain.enum.ExpenseStatus
import br.com.financialcontrol.domain.enum.PaymentMethod
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "expenses")
class ExpenseJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id") var userId: UUID = UUID.randomUUID(),
    @Column(name = "category_id") var categoryId: UUID = UUID.randomUUID(),
    @Column(name = "account_id") var accountId: UUID? = null,
    @Column(name = "credit_card_id") var creditCardId: UUID? = null,
    @Column(name = "subscription_id") var subscriptionId: UUID? = null,
    var description: String = "",
    var merchant: String? = null,
    @Column(name = "purchase_date") var purchaseDate: LocalDate = LocalDate.now(),
    @Column(name = "total_amount") var totalAmount: BigDecimal = BigDecimal.ZERO,
    @Enumerated(EnumType.STRING) @Column(name = "payment_method") var paymentMethod: PaymentMethod = PaymentMethod.PIX,
    @Enumerated(EnumType.STRING) var status: ExpenseStatus = ExpenseStatus.PENDING,
    @Column(name = "created_at") var createdAt: LocalDateTime = LocalDateTime.now(),
)
