package br.com.financialcontrol.domain.enums

enum class BankType {
    BANK, DIGITAL_WALLET, CASH
}

enum class AccountType {
    CHECKING, SAVINGS, CASH, INVESTMENT
}

enum class CategoryType {
    INCOME, EXPENSE
}

enum class TransactionType {
    INCOME, EXPENSE
}

enum class PaymentMethod {
    CREDIT_CARD, DEBIT_CARD, PIX, CASH, BANK_TRANSFER, BOLETO
}

enum class TransactionSource {
    MANUAL, CSV_IMPORT, OFX_IMPORT, SEED
}

enum class ImportFileType {
    CSV, OFX
}
