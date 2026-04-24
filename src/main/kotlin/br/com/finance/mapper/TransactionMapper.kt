package br.com.finance.mapper

import br.com.finance.dto.TransactionResponse
import br.com.finance.model.TransactionEntity

fun TransactionEntity.toResponse() = TransactionResponse(
    id = id,
    date = transactionDate,
    month = monthRef,
    bank = bank,
    card = card,
    description = description,
    merchant = merchant,
    category = category?.name,
    amount = amount,
    type = type,
    installmentNumber = installmentNumber,
    installmentTotal = installmentTotal
)
