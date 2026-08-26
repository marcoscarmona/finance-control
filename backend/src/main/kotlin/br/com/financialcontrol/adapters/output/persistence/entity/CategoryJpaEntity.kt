package br.com.financialcontrol.adapters.output.persistence.entity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "categories")
class CategoryJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "user_id") var userId: UUID = UUID.randomUUID(),
    var name: String = "",
    var color: String = "#888888",
    var active: Boolean = true,
)
