package br.com.financialcontrol.adapters.output.persistence.adapter

import br.com.financialcontrol.adapters.output.persistence.entity.UserJpaEntity
import br.com.financialcontrol.adapters.output.persistence.repository.UserSpringDataRepository
import br.com.financialcontrol.application.port.output.UserPersistencePort
import br.com.financialcontrol.domain.model.User
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class UserPersistenceAdapter(
    private val repository: UserSpringDataRepository,
) : UserPersistencePort {
    override fun save(user: User) = repository.save(UserJpaEntity(user.id, user.name, user.email, user.createdAt)).toDomain()

    override fun findById(id: UUID) = repository.findById(id).getOrNull()?.toDomain()

    override fun findByEmail(email: String) = repository.findByEmail(email)?.toDomain()

    override fun existsByEmail(email: String) = repository.existsByEmail(email)
}

private fun UserJpaEntity.toDomain() = User(id, name, email, createdAt)
