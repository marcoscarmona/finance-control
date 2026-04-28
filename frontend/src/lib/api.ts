import axios from 'axios'
import { DashboardData, Transaction, Bank, CreditCard, Category, Budget, User } from '@/types'

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
})

let cachedUserId: string | null = null

export async function getDemoUserId(): Promise<string> {
  if (cachedUserId) return cachedUserId
  const { data } = await api.get<User>('/api/users/demo')
  cachedUserId = data.id
  return data.id
}

export async function getDashboard(userId: string, yearMonth: string): Promise<DashboardData> {
  const { data } = await api.get(`/api/users/${userId}/dashboard`, { params: { yearMonth } })
  return data
}

export async function getTransactions(userId: string, yearMonth?: string, bankId?: string, categoryId?: string): Promise<Transaction[]> {
  const { data } = await api.get(`/api/users/${userId}/transactions`, {
    params: { yearMonth, bankId, categoryId },
  })
  return data
}

export async function createTransaction(userId: string, payload: Partial<Transaction>): Promise<Transaction> {
  const { data } = await api.post(`/api/users/${userId}/transactions`, payload)
  return data
}

export async function deleteTransaction(userId: string, transactionId: string): Promise<void> {
  await api.delete(`/api/users/${userId}/transactions/${transactionId}`)
}

export async function getBanks(userId: string): Promise<Bank[]> {
  const { data } = await api.get(`/api/users/${userId}/banks`)
  return data
}

export async function getCreditCards(userId: string): Promise<CreditCard[]> {
  const { data } = await api.get(`/api/users/${userId}/credit-cards`)
  return data
}

export async function getCategories(userId: string): Promise<Category[]> {
  const { data } = await api.get(`/api/users/${userId}/categories`)
  return data
}

export async function getBudgets(userId: string, yearMonth: string): Promise<Budget[]> {
  const { data } = await api.get(`/api/users/${userId}/budgets`, { params: { yearMonth } })
  return data
}

export async function importCsv(userId: string, file: File): Promise<{ imported: number; total: number }> {
  const form = new FormData()
  form.append('file', file)
  const { data } = await api.post(`/api/users/${userId}/imports/csv`, form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}
