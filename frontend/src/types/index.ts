export type BankType = 'BANK' | 'DIGITAL_WALLET' | 'CASH'
export type AccountType = 'CHECKING' | 'SAVINGS' | 'CASH' | 'INVESTMENT'
export type CategoryType = 'INCOME' | 'EXPENSE'
export type TransactionType = 'INCOME' | 'EXPENSE'
export type PaymentMethod = 'CREDIT_CARD' | 'DEBIT_CARD' | 'PIX' | 'CASH' | 'BANK_TRANSFER' | 'BOLETO'
export type TransactionSource = 'MANUAL' | 'CSV_IMPORT' | 'OFX_IMPORT' | 'SEED'

export interface User {
  id: string
  name: string
  email: string
  createdAt: string
}

export interface Bank {
  id: string
  userId: string
  name: string
  code?: string
  type: BankType
  createdAt: string
}

export interface CreditCard {
  id: string
  userId: string
  bankId: string
  name: string
  lastFourDigits: string
  limitAmount: number
  closingDay: number
  dueDay: number
  active: boolean
}

export interface Category {
  id: string
  userId?: string
  name: string
  type: CategoryType
  color: string
  icon: string
  active: boolean
}

export interface Transaction {
  id: string
  userId: string
  bankId?: string
  accountId?: string
  creditCardId?: string
  categoryId: string
  categoryName?: string
  categoryColor?: string
  bankName?: string
  date: string
  description: string
  merchant?: string
  amount: number
  type: TransactionType
  paymentMethod: PaymentMethod
  source: TransactionSource
  installmentNumber?: number
  installmentTotal?: number
  recurring: boolean
  createdAt: string
}

export interface Budget {
  id: string
  userId: string
  categoryId: string
  categoryName?: string
  yearMonth: string
  limitAmount: number
  spent: number
}

export interface ExpenseByCategory {
  categoryId: string
  categoryName: string
  color: string
  total: number
}

export interface ExpenseByBank {
  bankId?: string
  bankName: string
  total: number
}

export interface ExpenseByPaymentMethod {
  paymentMethod: PaymentMethod
  total: number
}

export interface DailyExpense {
  date: string
  total: number
}

export interface TopMerchant {
  merchant: string
  total: number
  count: number
}

export interface BudgetComparison {
  categoryId: string
  categoryName: string
  color: string
  limitAmount: number
  spent: number
  percentage: number
}

export interface DashboardData {
  totalIncome: number
  totalExpense: number
  balance: number
  expensesByCategory: ExpenseByCategory[]
  expensesByBank: ExpenseByBank[]
  expensesByPaymentMethod: ExpenseByPaymentMethod[]
  dailyExpenses: DailyExpense[]
  topMerchants: TopMerchant[]
  budgetComparison: BudgetComparison[]
  recentTransactions: Transaction[]
}
