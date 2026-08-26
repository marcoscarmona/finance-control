package br.com.financialcontrol.application

import br.com.financialcontrol.domain.service.InstallmentCalculator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

class InstallmentCalculatorTest {
    private val calculator = InstallmentCalculator()

    @Test
    fun `splits the remainder into the last installment`() {
        assertEquals(
            listOf(BigDecimal("33.33"), BigDecimal("33.33"), BigDecimal("33.34")),
            calculator.split(BigDecimal("100.00"), 3),
        )
    }

    @Test
    fun `purchase on closing day goes to next invoice month`() {
        assertEquals(YearMonth.of(2026, 6), calculator.referenceMonth(LocalDate.of(2026, 5, 25), 25))
    }
}
