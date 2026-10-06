package br.com.financialcontrol.adapters.output.persistence.repository

import br.com.financialcontrol.adapters.output.persistence.entity.InvestmentGoalJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.InvestmentPositionJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.ReceivableJpaEntity
import br.com.financialcontrol.adapters.output.persistence.entity.RecurringExpenseJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface InvestmentPositionRepository : JpaRepository<InvestmentPositionJpaEntity, UUID> { fun findAllByUserIdAndReferenceMonth(userId: UUID, referenceMonth: String): List<InvestmentPositionJpaEntity>; fun findAllByUserId(userId: UUID): List<InvestmentPositionJpaEntity> }
interface InvestmentGoalRepository : JpaRepository<InvestmentGoalJpaEntity, UUID> { fun findByUserId(userId: UUID): InvestmentGoalJpaEntity? }
interface ReceivableRepository : JpaRepository<ReceivableJpaEntity, UUID> { fun findAllByUserId(userId: UUID): List<ReceivableJpaEntity> }
interface RecurringExpenseRepository : JpaRepository<RecurringExpenseJpaEntity, UUID> { fun findAllByUserId(userId: UUID): List<RecurringExpenseJpaEntity> }
