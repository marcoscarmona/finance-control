package br.com.financialcontrol.infrastructure.config

import br.com.financialcontrol.application.FinanceRepository
import br.com.financialcontrol.application.FinanceService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration class AppConfig { @Bean fun financeService(repository: FinanceRepository) = FinanceService(repository) }
