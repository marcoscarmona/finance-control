package br.com.financialcontrol.domain.enum

enum class CardDateRule {
    FIXED_DAY,
    NTH_BUSINESS_DAY,
    NTH_BUSINESS_DAY_AFTER_CLOSING,
    NEXT_BUSINESS_DAY_ON_OR_AFTER,
}
