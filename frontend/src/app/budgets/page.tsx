'use client'
import { useEffect, useState } from 'react'
import { getDemoUserId, getBudgets } from '@/lib/api'
import { Budget } from '@/types'
import { Card } from '@/components/ui/Card'
import { formatCurrency } from '@/lib/utils'
import { Loader2 } from 'lucide-react'

const MONTHS = ['2026-04','2026-03','2026-02']

export default function BudgetsPage() {
  const [budgets, setBudgets] = useState<Budget[]>([])
  const [loading, setLoading] = useState(true)
  const [yearMonth, setYearMonth] = useState('2026-04')
  const [userId, setUserId] = useState('')

  useEffect(() => {
    getDemoUserId().then(uid => {
      setUserId(uid)
      return getBudgets(uid, '2026-04')
    }).then(setBudgets).finally(() => setLoading(false))
  }, [])

  async function handleMonthChange(ym: string) {
    setYearMonth(ym)
    setLoading(true)
    const b = await getBudgets(userId, ym)
    setBudgets(b)
    setLoading(false)
  }

  if (loading && !userId) return <div className="flex justify-center py-20"><Loader2 className="animate-spin text-sky-400" size={32} /></div>

  const totalLimit = budgets.reduce((s, b) => s + b.limitAmount, 0)
  const totalSpent = budgets.reduce((s, b) => s + b.spent, 0)

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Orçamentos</h1>
          <p className="text-slate-400 text-sm">Controle de gastos por categoria</p>
        </div>
        <select
          value={yearMonth}
          onChange={e => handleMonthChange(e.target.value)}
          className="bg-slate-800 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm"
        >
          {MONTHS.map(m => <option key={m} value={m}>{m}</option>)}
        </select>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div className="bg-slate-800 border border-slate-700 rounded-xl p-4">
          <p className="text-slate-400 text-sm">Total Orçado</p>
          <p className="text-white text-xl font-bold">{formatCurrency(totalLimit)}</p>
        </div>
        <div className="bg-slate-800 border border-slate-700 rounded-xl p-4">
          <p className="text-slate-400 text-sm">Total Gasto</p>
          <p className="text-red-400 text-xl font-bold">{formatCurrency(totalSpent)}</p>
        </div>
        <div className="bg-slate-800 border border-slate-700 rounded-xl p-4">
          <p className="text-slate-400 text-sm">Disponível</p>
          <p className={`text-xl font-bold ${totalLimit - totalSpent >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
            {formatCurrency(totalLimit - totalSpent)}
          </p>
        </div>
      </div>

      <Card>
        {loading ? (
          <div className="flex justify-center py-10"><Loader2 className="animate-spin text-sky-400" size={24} /></div>
        ) : (
          <div className="space-y-5">
            {budgets.map(b => {
              const pct = b.limitAmount > 0 ? Math.min((b.spent / b.limitAmount) * 100, 100) : 0
              const over = b.spent > b.limitAmount
              return (
                <div key={b.id}>
                  <div className="flex justify-between items-center mb-2">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full" style={{ backgroundColor: '#0ea5e9' }} />
                      <span className="text-slate-200 text-sm font-medium">{b.categoryName}</span>
                      {over && <span className="text-xs text-red-400 bg-red-400/10 px-2 py-0.5 rounded">Acima do limite</span>}
                    </div>
                    <div className="text-right text-sm">
                      <span className={over ? 'text-red-400' : 'text-slate-300'}>{formatCurrency(b.spent)}</span>
                      <span className="text-slate-500"> / {formatCurrency(b.limitAmount)}</span>
                    </div>
                  </div>
                  <div className="h-2.5 bg-slate-700 rounded-full overflow-hidden">
                    <div
                      className="h-full rounded-full transition-all duration-500"
                      style={{
                        width: `${pct}%`,
                        backgroundColor: over ? '#ef4444' : pct > 80 ? '#f59e0b' : '#0ea5e9',
                      }}
                    />
                  </div>
                  <p className="text-slate-500 text-xs mt-1">{pct.toFixed(0)}% utilizado</p>
                </div>
              )
            })}
            {budgets.length === 0 && (
              <p className="text-slate-500 text-center py-6">Nenhum orçamento para este mês</p>
            )}
          </div>
        )}
      </Card>
    </div>
  )
}
