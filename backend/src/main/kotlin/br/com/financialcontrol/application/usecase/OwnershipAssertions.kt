package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.port.output.UserPersistencePort
import java.util.UUID

internal fun requireUser(
    users: UserPersistencePort,
    userId: UUID,
) {
    require(users.findById(userId) != null) { "User not found" }
}

internal fun requireOwned(
    actual: UUID?,
    expected: UUID,
    label: String,
) {
    require(actual == expected) { "$label not found for user" }
}
