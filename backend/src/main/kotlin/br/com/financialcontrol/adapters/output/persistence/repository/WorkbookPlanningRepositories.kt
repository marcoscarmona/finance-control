package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.ExchangeRateJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.PersonalLoanJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.SharedExpenseJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PersonalLoanRepository : JpaRepository<PersonalLoanJpaEntity, UUID> { fun findAllByUserId(userId: UUID): List<PersonalLoanJpaEntity> }
interface SharedExpenseRepository : JpaRepository<SharedExpenseJpaEntity, UUID> { fun findAllByUserId(userId: UUID): List<SharedExpenseJpaEntity> }
interface ExchangeRateRepository : JpaRepository<ExchangeRateJpaEntity, UUID> { fun findAllByUserIdAndReferenceMonth(userId: UUID, referenceMonth: String): List<ExchangeRateJpaEntity> }
