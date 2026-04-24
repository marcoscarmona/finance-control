package br.com.finance.service

import br.com.finance.dto.CreateTransactionRequest
import br.com.finance.model.TransactionType
import br.com.finance.service.TransactionService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.LocalDate

@Service
class ImportService(
    private val transactionService: TransactionService
) {
    fun importCsv(file: MultipartFile): Int {
        val content = file.inputStream.bufferedReader().readText()
        return content.lineSequence()
            .drop(1)
            .filter { it.isNotBlank() }
            .mapNotNull { parseCsvLine(it) }
            .onEach { transactionService.create(it) }
            .count()
    }

    private fun parseCsvLine(line: String): CreateTransactionRequest? {
        val parts = line.split(",").map { it.trim() }
        if (parts.size < 5) return null
        return CreateTransactionRequest(
            date = LocalDate.parse(parts[0]),
            bank = parts[1],
            description = parts[2],
            amount = BigDecimal(parts[3].replace("R$", "").replace(".", "").replace(",", ".")),
            type = TransactionType.valueOf(parts[4].uppercase()),
            source = "csv"
        )
    }
}
