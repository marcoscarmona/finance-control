package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CreateUserCommand
import br.com.financialcontrol.application.port.output.UserPersistencePort
import br.com.financialcontrol.domain.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class UserApplicationServiceTest {
    @Test
    fun `creates a normalized user and prevents duplicate email`() {
        val users = InMemoryUsers()
        val service = UserApplicationService(users)
        val created = service.execute(CreateUserCommand(" Marcos ", "MARCOS@EXAMPLE.COM"))
        assertEquals("Marcos", created.name)
        assertEquals("marcos@example.com", created.email)
        assertThrows(IllegalArgumentException::class.java) {
            service.execute(
                CreateUserCommand(
                    "Other",
                    "marcos@example.com",
                ),
            )
        }
    }

    private class InMemoryUsers : UserPersistencePort {
        private val values = mutableMapOf<UUID, User>()

        override fun save(user: User): User = user.also { values[it.id] = it }

        override fun findById(id: UUID): User? = values[id]

        override fun existsByEmail(email: String): Boolean = values.values.any { it.email == email }
    }
}
