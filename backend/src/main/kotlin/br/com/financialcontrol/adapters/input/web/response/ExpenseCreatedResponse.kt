package br.com.financialcontrol.adapters.input.web.response

import br.com.financialcontrol.domain.model.Expense
import br.com.financialcontrol.domain.model.ExpenseInstallment

data class ExpenseCreatedResponse(
    val expense: Expense,
    val installments: List<ExpenseInstallment>,
)
