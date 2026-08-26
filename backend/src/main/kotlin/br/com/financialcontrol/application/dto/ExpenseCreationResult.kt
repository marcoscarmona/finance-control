package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.model.Expense
import br.com.financialcontrol.domain.model.ExpenseInstallment

data class ExpenseCreationResult(
    val expense: Expense,
    val installments: List<ExpenseInstallment>,
)
