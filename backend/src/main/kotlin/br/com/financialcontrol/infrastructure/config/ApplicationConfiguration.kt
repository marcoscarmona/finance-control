package br.com.financialcontrol.infrastructure.config

import br.com.financialcontrol.application.port.output.AccountPersistencePort
import br.com.financialcontrol.application.port.output.BankPersistencePort
import br.com.financialcontrol.application.port.output.CardInvoiceManualTotalPersistencePort
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
import br.com.financialcontrol.application.usecase.ManualCardInvoiceTotalApplicationService
import br.com.financialcontrol.application.usecase.MonthlyReportApplicationService
import br.com.financialcontrol.application.usecase.SubscriptionApplicationService
import br.com.financialcontrol.application.usecase.UserApplicationService
import br.com.financialcontrol.domain.service.CardCalendar
import br.com.financialcontrol.domain.service.InstallmentCalculator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {
    @Bean
    fun installmentCalculator() = InstallmentCalculator()

    @Bean
    fun cardCalendar() = CardCalendar()

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
        calendar: CardCalendar,
    ) = CreditCardApplicationService(banks, cards, invoices, installments, calculator, calendar)

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
        calendar: CardCalendar,
    ) = ExpenseApplicationService(categories, accounts, cards, expenses, installments, invoices, calculator, calendar)

    @Bean
    fun monthlyReportApplicationService(
        users: UserPersistencePort,
        installments: ExpenseInstallmentPersistencePort,
        expenses: ExpensePersistencePort,
        categories: CategoryPersistencePort,
        cards: CreditCardPersistencePort,
        subscriptions: SubscriptionPersistencePort,
        manualTotals: CardInvoiceManualTotalPersistencePort,
    ) = MonthlyReportApplicationService(users, installments, expenses, categories, cards, subscriptions, manualTotals)

    @Bean
    fun manualCardInvoiceTotalApplicationService(
        cards: CreditCardPersistencePort,
        totals: CardInvoiceManualTotalPersistencePort,
    ) = ManualCardInvoiceTotalApplicationService(cards, totals)
}
