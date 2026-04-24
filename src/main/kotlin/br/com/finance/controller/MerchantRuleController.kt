package br.com.finance.controller

import br.com.finance.dto.MerchantRuleRequest
import br.com.finance.dto.MerchantRuleResponse
import br.com.finance.model.MerchantRuleEntity
import br.com.carmona.finance.repository.CategoryRepository
import br.com.carmona.finance.repository.MerchantRuleRepository
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rules")
class MerchantRuleController(
    private val merchantRuleRepository: MerchantRuleRepository,
    private val categoryRepository: CategoryRepository
) {
    @GetMapping
    fun list() = merchantRuleRepository.findAllByOrderByPriorityAsc().map {
        MerchantRuleResponse(it.id, it.keyword, it.category.name, it.priority)
    }

    @PostMapping
    fun create(@RequestBody @Valid request: MerchantRuleRequest): MerchantRuleResponse {
        val category = categoryRepository.getReferenceById(request.categoryId)
        val saved = merchantRuleRepository.save(
            MerchantRuleEntity(
                keyword = request.keyword.trim().uppercase(),
                category = category,
                priority = request.priority
            )
        )
        return MerchantRuleResponse(saved.id, saved.keyword, saved.category.name, saved.priority)
    }
}
