package br.com.financialcontrol.adapters.input.web.controller

import br.com.financialcontrol.application.port.input.GenerateMonthlyReportUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.YearMonth
import java.util.UUID

@RestController
@RequestMapping("/api/users/{userId}/reports")
class ReportController(
    private val report: GenerateMonthlyReportUseCase,
) {
    @GetMapping("/monthly")
    fun monthly(
        @PathVariable userId: UUID,
        @RequestParam year: Int,
        @RequestParam month: Int,
    ) = report.execute(userId, YearMonth.of(year, month))
}
