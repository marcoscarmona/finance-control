package br.com.finance.repository

import br.com.finance.model.CategoryEntity
import br.com.finance.model.InstallmentEntity
import br.com.finance.model.MerchantRuleEntity
import br.com.finance.model.SubscriptionEntity
import br.com.finance.model.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface CategoryRepository : JpaRepository<CategoryEntity, UUID> {
    fun findByNameIgnoreCase(name: String): CategoryEntity?
}

interface MerchantRuleRepository : JpaRepository<MerchantRuleEntity, UUID> {
    fun findAllByOrderByPriorityAsc(): List<MerchantRuleEntity>
}

interface TransactionRepository : JpaRepository<TransactionEntity, UUID> {
    fun findByMonthRefOrderByTransactionDateAsc(monthRef: String): List<TransactionEntity>
    fun findByMonthRefAndBankIgnoreCaseOrderByTransactionDateAsc(monthRef: String, bank: String): List<TransactionEntity>

    @Query("""
        select coalesce(c.name, 'Sem categoria'), sum(t.amount)
        from TransactionEntity t
        left join t.category c
        where t.monthRef = :month and t.type = br.com.carmona.finance.model.TransactionType.DEBIT
        group by c.name
        order by sum(t.amount) desc
    """)
    fun sumByCategory(@Param("month") month: String): List<Array<Any>>

    @Query("""
        select t.bank, sum(t.amount)
        from TransactionEntity t
        where t.monthRef = :month and t.type = br.com.carmona.finance.model.TransactionType.DEBIT
        group by t.bank
        order by sum(t.amount) desc
    """)
    fun sumByBank(@Param("month") month: String): List<Array<Any>>

    @Query("""
        select coalesce(t.merchant, t.description), sum(t.amount)
        from TransactionEntity t
        where t.monthRef = :month and t.type = br.com.carmona.finance.model.TransactionType.DEBIT
        group by coalesce(t.merchant, t.description)
        order by sum(t.amount) desc
    """)
    fun topMerchants(@Param("month") month: String): List<Array<Any>>
}

interface SubscriptionRepository : JpaRepository<SubscriptionEntity, UUID>
interface InstallmentRepository : JpaRepository<InstallmentEntity, UUID>
