package br.com.financialcontrol.infrastructure.config

import br.com.financialcontrol.application.port.output.AccountPersistencePort
import br.com.financialcontrol.application.port.output.BankPersistencePort
import br.com.financialcontrol.application.port.output.CardInvoicePersistencePort
import br.com.financialcontrol.application.port.output.CategoryPersistencePort
import br.com.financialcontrol.application.port.output.CreditCardPersistencePort
import br.com.financialcontrol.application.port.output.ExpenseInstallmentPersistencePort
import br.com.financialcontrol.application.port.output.ExpensePersistencePort
import br.com.financialcontrol.application.port.output.SubscriptionPersistencePort
import br.com.financialcontrol.application.port.output.UserPersistencePort
import br.com.financialcontrol.application.usecase.AccountApplicationService
import br.com.financialcontrol.application.usecase.BankApplicationService
import br.com.financialcontrol.application.usecase.CategoryApplicationService
import br.com.financialcontrol.application.usecase.CreditCardApplicationService
import br.com.financialcontrol.application.usecase.ExpenseApplicationService
import br.com.financialcontrol.application.usecase.MonthlyReportApplicationService
import br.com.financialcontrol.application.usecase.SubscriptionApplicationService
import br.com.financialcontrol.application.usecase.UserApplicationService
import br.com.financialcontrol.domain.service.InstallmentCalculator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {
    @Bean
    fun installmentCalculator() = InstallmentCalculator()

    @Bean
    fun userApplicationService(users: UserPersistencePort) = UserApplicationService(users)

    @Bean
    fun bankApplicationService(
        users: UserPersistencePort,
        banks: BankPersistencePort,
    ) = BankApplicationService(users, banks)

    @Bean
    fun accountApplicationService(
        banks: BankPersistencePort,
        accounts: AccountPersistencePort,
    ) = AccountApplicationService(banks, accounts)

    @Bean
    fun categoryApplicationService(
        users: UserPersistencePort,
        categories: CategoryPersistencePort,
    ) = CategoryApplicationService(users, categories)

    @Bean
    fun creditCardApplicationService(
        banks: BankPersistencePort,
        cards: CreditCardPersistencePort,
        invoices: CardInvoicePersistencePort,
        installments: ExpenseInstallmentPersistencePort,
        calculator: InstallmentCalculator,
    ) = CreditCardApplicationService(banks, cards, invoices, installments, calculator)

    @Bean
    fun subscriptionApplicationService(
        categories: CategoryPersistencePort,
        cards: CreditCardPersistencePort,
        accounts: AccountPersistencePort,
        subscriptions: SubscriptionPersistencePort,
    ) = SubscriptionApplicationService(categories, cards, accounts, subscriptions)

    @Bean
    fun expenseApplicationService(
        categories: CategoryPersistencePort,
        accounts: AccountPersistencePort,
        cards: CreditCardPersistencePort,
        expenses: ExpensePersistencePort,
        installments: ExpenseInstallmentPersistencePort,
        invoices: CardInvoicePersistencePort,
        calculator: InstallmentCalculator,
    ) = ExpenseApplicationService(categories, accounts, cards, expenses, installments, invoices, calculator)

    @Bean
    fun monthlyReportApplicationService(
        users: UserPersistencePort,
        installments: ExpenseInstallmentPersistencePort,
        expenses: ExpensePersistencePort,
        categories: CategoryPersistencePort,
        cards: CreditCardPersistencePort,
        subscriptions: SubscriptionPersistencePort,
    ) = MonthlyReportApplicationService(users, installments, expenses, categories, cards, subscriptions)
}
