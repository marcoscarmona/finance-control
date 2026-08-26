package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserSpringDataRepository : JpaRepository<UserJpaEntity, UUID> {
    fun existsByEmail(email: String): Boolean
}
