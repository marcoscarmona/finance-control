package br.com.financialcontrol.domain.service

import br.com.financialcontrol.domain.enum.CardDateRule
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth

class InstallmentCalculator {
    fun split(
        total: BigDecimal,
        count: Int,
    ): List<BigDecimal> {
        require(count > 0)
        require(total.compareTo(BigDecimal.ZERO) != 0)
        val base = total.divide(BigDecimal(count), 2, RoundingMode.DOWN)
        val remainder = total - base * BigDecimal(count)
        return List(count) { if (it == count - 1) base + remainder else base }
    }

    fun referenceMonth(
        date: LocalDate,
        closingDay: Int,
        closingRule: CardDateRule = CardDateRule.FIXED_DAY,
        calendar: CardCalendar = CardCalendar(),
    ): YearMonth {
        val currentMonth = YearMonth.from(date)
        val closingDate = calendar.closingDate(currentMonth, closingDay, closingRule)
        return if (date >= closingDate) currentMonth.plusMonths(1) else currentMonth
    }
}
