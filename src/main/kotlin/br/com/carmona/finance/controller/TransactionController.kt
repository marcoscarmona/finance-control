package br.com.carmona.finance.controller

import br.com.carmona.finance.dto.CreateTransactionRequest
import br.com.carmona.finance.service.TransactionService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {
    @PostMapping
    fun create(@RequestBody @Valid request: CreateTransactionRequest) = transactionService.create(request)

    @GetMapping
    fun list(
        @RequestParam month: String,
        @RequestParam(required = false) bank: String?
    ) = transactionService.list(month, bank)
}
