package br.com.financialcontrol.adapters.output.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    var name: String = "",
    var email: String = "",
    @Column(name = "created_at") var createdAt: LocalDateTime = LocalDateTime.now(),
)
