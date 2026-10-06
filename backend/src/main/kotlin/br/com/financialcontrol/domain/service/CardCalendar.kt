package br.com.financialcontrol.domain.service

import br.com.financialcontrol.domain.enum.CardDateRule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class CardCalendar {
    fun closingDate(
        month: YearMonth,
        day: Int,
        rule: CardDateRule,
    ): LocalDate = dateFor(month, day, rule, null)

    fun dueDate(
        month: YearMonth,
        day: Int,
        rule: CardDateRule,
        closingDate: LocalDate,
    ): LocalDate =
        if (rule == CardDateRule.FIXED_DAY) {
            month.plusMonths(1).atDay(day.coerceAtMost(month.plusMonths(1).lengthOfMonth()))
        } else {
            dateFor(month, day, rule, closingDate)
        }

    private fun dateFor(
        month: YearMonth,
        day: Int,
        rule: CardDateRule,
        closingDate: LocalDate?,
    ): LocalDate =
        when (rule) {
            CardDateRule.FIXED_DAY -> month.atDay(day.coerceAtMost(month.lengthOfMonth()))
            CardDateRule.NTH_BUSINESS_DAY -> nthBusinessDay(month.atDay(1), day)
            CardDateRule.NEXT_BUSINESS_DAY_ON_OR_AFTER -> nextBusinessDay(month.atDay(day.coerceAtMost(month.lengthOfMonth())))
            CardDateRule.NTH_BUSINESS_DAY_AFTER_CLOSING -> nthBusinessDay(requireNotNull(closingDate).plusDays(1), day)
        }

    private fun nthBusinessDay(
        start: LocalDate,
        occurrence: Int,
    ): LocalDate {
        require(occurrence > 0) { "Business day occurrence must be positive" }
        var candidate = start
        var found = 0
        while (found < occurrence) {
            if (candidate.isBusinessDay()) found += 1
            if (found < occurrence) candidate = candidate.plusDays(1)
        }
        return candidate
    }

    private fun nextBusinessDay(date: LocalDate): LocalDate {
        var candidate = date
        while (!candidate.isBusinessDay()) candidate = candidate.plusDays(1)
        return candidate
    }

    private fun LocalDate.isBusinessDay() = dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
}
