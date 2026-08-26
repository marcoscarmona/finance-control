package br.com.financialcontrol.application.port.input

import br.com.financialcontrol.application.dto.MonthlyReport
import java.time.YearMonth
import java.util.UUID

interface GenerateMonthlyReportUseCase {
    fun execute(
        userId: UUID,
        month: YearMonth,
    ): MonthlyReport
}
