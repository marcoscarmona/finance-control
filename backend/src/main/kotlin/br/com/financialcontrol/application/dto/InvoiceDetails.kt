package br.com.financialcontrol.application.dto

import br.com.financialcontrol.domain.model.CardInvoice
import br.com.financialcontrol.domain.model.ExpenseInstallment

data class InvoiceDetails(
    val invoice: CardInvoice,
    val installments: List<ExpenseInstallment>,
)
