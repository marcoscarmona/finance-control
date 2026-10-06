package br.com.financialcontrol.domain.service

import br.com.financialcontrol.domain.enum.CardDateRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.YearMonth

class CardCalendarTest {
    private val calendar = CardCalendar()

    @Test
    fun `calculates Nubank fifth business day after closing`() {
        val closing = calendar.closingDate(YearMonth.of(2026, 10), 26, CardDateRule.FIXED_DAY)

        assertEquals(LocalDate.of(2026, 10, 26), closing)
        assertEquals(
            LocalDate.of(2026, 11, 2),
            calendar.dueDate(YearMonth.of(2026, 10), 5, CardDateRule.NTH_BUSINESS_DAY_AFTER_CLOSING, closing),
        )
    }

    @Test
    fun `calculates Mercado Pago business day calendar rules`() {
        val month = YearMonth.of(2027, 2)
        val closing = calendar.closingDate(month, 5, CardDateRule.NTH_BUSINESS_DAY)

        assertEquals(LocalDate.of(2027, 2, 5), closing)
        assertEquals(
            LocalDate.of(2027, 2, 15),
            calendar.dueDate(month, 13, CardDateRule.NEXT_BUSINESS_DAY_ON_OR_AFTER, closing),
        )
    }

    @Test
    fun `uses the fixed due day in the invoice reference month`() {
        val month = YearMonth.of(2026, 10)
        val closing = calendar.closingDate(month, 5, CardDateRule.FIXED_DAY)

        assertEquals(LocalDate.of(2026, 10, 13), calendar.dueDate(month, 13, CardDateRule.FIXED_DAY, closing))
    }

    @Test
    fun `moves purchase on business closing date into next invoice`() {
        val calculator = InstallmentCalculator()

        assertEquals(
            YearMonth.of(2027, 3),
            calculator.referenceMonth(
                LocalDate.of(2027, 2, 5),
                5,
                CardDateRule.NTH_BUSINESS_DAY,
                calendar,
            ),
        )
    }
}
