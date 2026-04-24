package br.com.carmona.finance.service

import br.com.carmona.finance.model.CategoryEntity
import br.com.carmona.finance.repository.CategoryRepository
import br.com.carmona.finance.repository.MerchantRuleRepository
import org.springframework.stereotype.Service

@Service
class CategoryClassifierService(
    private val merchantRuleRepository: MerchantRuleRepository,
    private val categoryRepository: CategoryRepository
) {
    fun classify(description: String): CategoryEntity? {
        val normalized = description.uppercase()
        return merchantRuleRepository.findAllByOrderByPriorityAsc()
            .firstOrNull { normalized.contains(it.keyword.uppercase()) }
            ?.category
            ?: categoryRepository.findByNameIgnoreCase("Outros")
    }
}
