package br.com.carmona.finance.service

import br.com.carmona.finance.dto.CreateTransactionRequest
import br.com.carmona.finance.mapper.toResponse
import br.com.carmona.finance.model.TransactionEntity
import br.com.carmona.finance.repository.CategoryRepository
import br.com.carmona.finance.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val classifierService: CategoryClassifierService
) {
    @Transactional
    fun create(request: CreateTransactionRequest) = transactionRepository.save(
        TransactionEntity(
            transactionDate = request.date,
            monthRef = request.date.format(DateTimeFormatter.ofPattern("yyyy-MM")),
            bank = request.bank.trim(),
            card = request.card?.trim(),
            description = request.description.trim(),
            merchant = request.merchant?.trim() ?: extractMerchant(request.description),
            category = request.categoryId?.let { categoryRepository.getReferenceById(it) }
                ?: classifierService.classify(request.description),
            amount = request.amount.abs(),
            type = request.type,
            installmentNumber = request.installmentNumber,
            installmentTotal = request.installmentTotal,
            source = request.source
        )
    ).toResponse()

    @Transactional(readOnly = true)
    fun list(month: String, bank: String?) = when {
        bank.isNullOrBlank() -> transactionRepository.findByMonthRefOrderByTransactionDateAsc(month)
        else -> transactionRepository.findByMonthRefAndBankIgnoreCaseOrderByTransactionDateAsc(month, bank)
    }.map { it.toResponse() }

    private fun extractMerchant(description: String): String = description
        .replace(Regex("\\s+-\\s+Parcela\\s+\\d+/\\d+", RegexOption.IGNORE_CASE), "")
        .replace(Regex("\\s+USD.*", RegexOption.IGNORE_CASE), "")
        .trim()
        .take(160)
}
