package br.com.financialcontrol.infrastructure.persistence.repositories

import br.com.financialcontrol.infrastructure.persistence.entities.*
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface SystemUserJpaRepository : JpaRepository<SystemUserJpa, UUID> {
    fun findByEmail(email: String): SystemUserJpa?
    @Query("SELECT u FROM SystemUserJpa u WHERE u.email = 'marcos.demo@email.com'")
    fun findDemo(): SystemUserJpa?
}

@Repository
interface BankJpaRepository : JpaRepository<BankJpa, UUID> {
    fun findAllByUserId(userId: UUID): List<BankJpa>
}

@Repository
interface AccountJpaRepository : JpaRepository<AccountJpa, UUID> {
    fun findAllByUserId(userId: UUID): List<AccountJpa>
}

@Repository
interface CreditCardJpaRepository : JpaRepository<CreditCardJpa, UUID> {
    fun findAllByUserId(userId: UUID): List<CreditCardJpa>
}

@Repository
interface CategoryJpaRepository : JpaRepository<CategoryJpa, UUID> {
    @Query("SELECT c FROM CategoryJpa c WHERE c.userId = :userId OR c.userId IS NULL")
    fun findAllByUserIdOrDefault(@Param("userId") userId: UUID): List<CategoryJpa>
}

@Repository
interface TransactionJpaRepository : JpaRepository<TransactionJpa, UUID> {
    @Query("""
        SELECT t FROM TransactionJpa t 
        WHERE t.userId = :userId 
        AND TO_CHAR(t.date, 'YYYY-MM') = :yearMonth
        AND (:bankId IS NULL OR t.bankId = :bankId)
        AND (:categoryId IS NULL OR t.categoryId = :categoryId)
        ORDER BY t.date DESC
    """)
    fun findByUserIdAndYearMonth(
        @Param("userId") userId: UUID,
        @Param("yearMonth") yearMonth: String,
        @Param("bankId") bankId: UUID?,
        @Param("categoryId") categoryId: UUID?
    ): List<TransactionJpa>

    @Query("SELECT t FROM TransactionJpa t WHERE t.userId = :userId ORDER BY t.createdAt DESC")
    fun findRecentByUserId(@Param("userId") userId: UUID, pageable: Pageable): List<TransactionJpa>

    @Query("SELECT t FROM TransactionJpa t WHERE t.userId = :userId AND t.date BETWEEN :start AND :end ORDER BY t.date DESC")
    fun findByUserIdAndDateBetween(
        @Param("userId") userId: UUID,
        @Param("start") start: LocalDate,
        @Param("end") end: LocalDate
    ): List<TransactionJpa>
}

@Repository
interface MonthlyBudgetJpaRepository : JpaRepository<MonthlyBudgetJpa, UUID> {
    fun findAllByUserIdAndYearMonth(userId: UUID, yearMonth: String): List<MonthlyBudgetJpa>
}

@Repository
interface ImportFileJpaRepository : JpaRepository<ImportFileJpa, UUID>
