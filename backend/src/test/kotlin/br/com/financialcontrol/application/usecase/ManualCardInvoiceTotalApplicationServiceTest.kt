package br.com.financialcontrol.application.usecase

import br.com.financialcontrol.application.dto.SetCurrentCardInvoiceTotalCommand
import br.com.financialcontrol.application.port.output.CardInvoiceManualTotalPersistencePort
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.domain.model.CardInvoiceManualTotal
import br.com.financialcontrol.domain.model.CreditCard
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.YearMonth
import java.util.UUID

class ManualCardInvoiceTotalApplicationServiceTest {
    @Test
    fun `stores and replaces the current month manual total for the card owner`() {
        val userId = UUID.randomUUID()
        val card = creditCard(userId)
        val totals = InMemoryTotals()
        val service = ManualCardInvoiceTotalApplicationService(Cards(card), totals)

        service.execute(userId, card.id, SetCurrentCardInvoiceTotalCommand(BigDecimal("1200.00")))
        val updated = service.execute(userId, card.id, SetCurrentCardInvoiceTotalCommand(BigDecimal("1350.75")))

        assertEquals(BigDecimal("1350.75"), updated.totalAmount)
        assertEquals(1, totals.values.size)
        assertEquals(YearMonth.now(), updated.referenceMonth)
    }

    @Test
    fun `rejects a total update for a card from another user`() {
        val service = ManualCardInvoiceTotalApplicationService(Cards(creditCard(UUID.randomUUID())), InMemoryTotals())

        assertThrows(IllegalArgumentException::class.java) {
            service.execute(UUID.randomUUID(), UUID.randomUUID(), SetCurrentCardInvoiceTotalCommand(BigDecimal.ONE))
        }
    }

    private fun creditCard(userId: UUID) =
        CreditCard(UUID.randomUUID(), userId, UUID.randomUUID(), "Nubank", "9526", BigDecimal("40000.00"), 26, 5)

    private class Cards(
        private val card: CreditCard,
    ) : CreditCardPersistencePort {
        override fun save(card: CreditCard) = card

        override fun findById(id: UUID) = card.takeIf { it.id == id }

        override fun findAllByUserId(userId: UUID) = listOf(card).filter { it.userId == userId }
    }

    private class InMemoryTotals : CardInvoiceManualTotalPersistencePort {
        val values = mutableMapOf<Pair<UUID, YearMonth>, CardInvoiceManualTotal>()

        override fun save(total: CardInvoiceManualTotal) =
            total.also { values[it.creditCardId to it.referenceMonth] = it }

        override fun findByCardIdAndReferenceMonth(
            cardId: UUID,
            month: YearMonth,
        ) = values[cardId to month]
    }
}
