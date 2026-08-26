package br.com.financialcontrol.application.port.input

import br.com.financialcontrol.application.dto.CreateExpenseCommand
import br.com.financialcontrol.application.dto.ExpenseCreationResult
import br.com.financialcontrol.domain.model.Expense
import java.util.UUID

interface CreateExpenseUseCase {
    fun execute(
        userId: UUID,
        command: CreateExpenseCommand,
    ): ExpenseCreationResult
}

interface ListExpensesUseCase {
    fun execute(userId: UUID): List<Expense>
}
