package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.CreateSubscriptionCommand
import br.com.financialcontrol.application.port.input.CreateSubscriptionUseCase
import br.com.financialcontrol.application.port.input.ListSubscriptionsUseCase
import br.com.financialcontrol.application.port.output.AccountPersistencePort
import br.com.financialcontrol.application.port.output.CategoryPersistencePort
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.application.port.output.SubscriptionPersistencePort
import br.com.financialcontrol.domain.model.Subscription
import java.util.UUID

class SubscriptionApplicationService(
    private val categories: CategoryPersistencePort,
    private val cards: CreditCardPersistencePort,
    private val accounts: AccountPersistencePort,
    private val subscriptions: SubscriptionPersistencePort,
) : CreateSubscriptionUseCase,
    ListSubscriptionsUseCase {
    override fun execute(
        userId: UUID,
        command: CreateSubscriptionCommand,
    ): Subscription {
        requireOwned(categories.findById(command.categoryId)?.userId, userId, "Category")
        command.creditCardId?.let { requireOwned(cards.findById(it)?.userId, userId, "Credit card") }
        command.accountId?.let { requireOwned(accounts.findById(it)?.userId, userId, "Account") }
        require(command.amount > java.math.BigDecimal.ZERO && command.chargeDay in 1..31) { "invalid subscription amount or charge day" }
        return subscriptions.save(
            Subscription(
                UUID.randomUUID(),
                userId,
                command.categoryId,
                command.creditCardId,
                command.accountId,
                command.name,
                command.amount,
                command.frequency,
                command.chargeDay,
            ),
        )
    }

    override fun execute(userId: UUID): List<Subscription> = subscriptions.findAllByUserId(userId)
}
