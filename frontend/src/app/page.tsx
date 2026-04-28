'use client'
import { useEffect, useState } from 'react'
import { getDemoUserId, getDashboard } from '@/lib/api'
import { DashboardData } from '@/types'
import { formatCurrency, formatDate, paymentMethodLabel } from '@/lib/utils'
import { StatCard, Card } from '@/components/ui/Card'
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend, AreaChart, Area
} from 'recharts'
import { TrendingUp, TrendingDown, Wallet, Tag, ArrowUpRight, Loader2 } from 'lucide-react'

const MONTHS = [
  { value: '2026-04', label: 'Abril 2026' },
  { value: '2026-03', label: 'Março 2026' },
  { value: '2026-02', label: 'Fevereiro 2026' },
  { value: '2026-01', label: 'Janeiro 2026' },
]

export default function DashboardPage() {
  const [data, setData] = useState<DashboardData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [yearMonth, setYearMonth] = useState('2026-04')

  useEffect(() => {
    async function load() {
      try {
        setLoading(true)
        const uid = await getDemoUserId()
        const d = await getDashboard(uid, yearMonth)
        setData(d)
      } catch (e: any) {
        setError(e?.message ?? 'Erro ao carregar dashboard')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [yearMonth])

  if (loading) return (
    <div className="flex items-center justify-center h-full">
      <Loader2 className="animate-spin text-sky-400" size={40} />
    </div>
  )

  if (error) return (
    <div className="flex items-center justify-center h-full">
      <div className="text-center">
        <p className="text-red-400 text-lg mb-2">Erro ao carregar dashboard</p>
        <p className="text-slate-500 text-sm">{error}</p>
        <p className="text-slate-500 text-sm mt-2">Certifique-se que o backend está rodando em http://localhost:8080</p>
      </div>
    </div>
  )

  if (!data) return null

  const topCategory = data.expensesByCategory[0]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Dashboard</h1>
          <p className="text-slate-400 text-sm">Visão geral financeira</p>
        </div>
        <select
          value={yearMonth}
          onChange={e => setYearMonth(e.target.value)}
          className="bg-slate-800 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-sky-500"
        >
          {MONTHS.map(m => <option key={m.value} value={m.value}>{m.label}</option>)}
        </select>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          label="Total de Receitas"
          value={formatCurrency(data.totalIncome)}
          color="text-emerald-400"
          icon={<TrendingUp size={22} />}
        />
        <StatCard
          label="Total de Gastos"
          value={formatCurrency(data.totalExpense)}
          color="text-red-400"
          icon={<TrendingDown size={22} />}
        />
        <StatCard
          label="Saldo"
          value={formatCurrency(data.balance)}
          color={data.balance >= 0 ? 'text-sky-400' : 'text-red-400'}
          icon={<Wallet size={22} />}
        />
        <StatCard
          label="Maior Categoria"
          value={topCategory?.categoryName ?? '-'}
          sub={topCategory ? formatCurrency(topCategory.total) : ''}
          icon={<Tag size={22} />}
        />
      </div>

      {/* Charts row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <Card title="Gastos por Categoria">
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={data.expensesByCategory} layout="vertical" margin={{ left: 20 }}>
              <XAxis type="number" hide />
              <YAxis type="category" dataKey="categoryName" width={90} tick={{ fill: '#94a3b8', fontSize: 12 }} />
              <Tooltip
                formatter={(v: number) => formatCurrency(v)}
                contentStyle={{ background: '#1e293b', border: '1px solid #334155', borderRadius: 8 }}
                labelStyle={{ color: '#f1f5f9' }}
              />
              <Bar dataKey="total" radius={[0, 4, 4, 0]}>
                {data.expensesByCategory.map((e, i) => (
                  <Cell key={i} fill={e.color} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </Card>

        <Card title="Gasto Diário">
          <ResponsiveContainer width="100%" height={220}>
            <AreaChart data={data.dailyExpenses.map(d => ({ ...d, dateLabel: d.date.slice(8) }))}>
              <XAxis dataKey="dateLabel" tick={{ fill: '#94a3b8', fontSize: 11 }} />
              <YAxis hide />
              <Tooltip
                formatter={(v: number) => formatCurrency(v)}
                contentStyle={{ background: '#1e293b', border: '1px solid #334155', borderRadius: 8 }}
                labelStyle={{ color: '#f1f5f9' }}
              />
              <Area type="monotone" dataKey="total" stroke="#0ea5e9" fill="#0ea5e9" fillOpacity={0.15} strokeWidth={2} />
            </AreaChart>
          </ResponsiveContainer>
        </Card>
      </div>

      {/* Charts row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <Card title="Gastos por Banco">
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie data={data.expensesByBank} dataKey="total" nameKey="bankName" cx="50%" cy="50%" outerRadius={70} label={({ bankName, percent }) => `${bankName} ${(percent * 100).toFixed(0)}%`} labelLine={false}>
                {data.expensesByBank.map((_, i) => (
                  <Cell key={i} fill={['#0ea5e9', '#8b5cf6', '#f59e0b', '#10b981'][i % 4]} />
                ))}
              </Pie>
              <Tooltip formatter={(v: number) => formatCurrency(v)} contentStyle={{ background: '#1e293b', border: '1px solid #334155', borderRadius: 8 }} />
            </PieChart>
          </ResponsiveContainer>
        </Card>

        <Card title="Por Forma de Pagamento">
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={data.expensesByPaymentMethod.map(d => ({ ...d, label: paymentMethodLabel(d.paymentMethod) }))}>
              <XAxis dataKey="label" tick={{ fill: '#94a3b8', fontSize: 10 }} />
              <YAxis hide />
              <Tooltip formatter={(v: number) => formatCurrency(v)} contentStyle={{ background: '#1e293b', border: '1px solid #334155', borderRadius: 8 }} />
              <Bar dataKey="total" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </Card>

        <Card title="Top Estabelecimentos">
          <div className="space-y-2">
            {data.topMerchants.slice(0, 5).map((m, i) => (
              <div key={i} className="flex items-center justify-between py-1">
                <div className="flex items-center gap-2">
                  <span className="text-slate-500 text-xs w-4">{i + 1}</span>
                  <span className="text-slate-300 text-sm truncate max-w-[130px]">{m.merchant}</span>
                </div>
                <div className="text-right">
                  <p className="text-white text-sm font-medium">{formatCurrency(m.total)}</p>
                  <p className="text-slate-500 text-xs">{m.count}x</p>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>

      {/* Budget comparison */}
      {data.budgetComparison.length > 0 && (
        <Card title="Orçamento vs Realizado">
          <div className="space-y-3">
            {data.budgetComparison.map((b, i) => (
              <div key={i}>
                <div className="flex justify-between text-sm mb-1">
                  <span className="text-slate-300">{b.categoryName}</span>
                  <span className={b.percentage > 100 ? 'text-red-400' : 'text-slate-400'}>
                    {formatCurrency(b.spent)} / {formatCurrency(b.limitAmount)}
                  </span>
                </div>
                <div className="h-2 bg-slate-700 rounded-full overflow-hidden">
                  <div
                    className="h-full rounded-full transition-all"
                    style={{
                      width: `${Math.min(b.percentage, 100)}%`,
                      backgroundColor: b.percentage > 100 ? '#ef4444' : b.percentage > 80 ? '#f59e0b' : b.color,
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* Recent transactions */}
      <Card title="Transações Recentes">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="text-slate-500 border-b border-slate-700">
                <th className="text-left py-2 font-medium">Data</th>
                <th className="text-left py-2 font-medium">Descrição</th>
                <th className="text-left py-2 font-medium">Categoria</th>
                <th className="text-left py-2 font-medium">Banco</th>
                <th className="text-right py-2 font-medium">Valor</th>
              </tr>
            </thead>
            <tbody>
              {data.recentTransactions.map(tx => (
                <tr key={tx.id} className="border-b border-slate-700/50 hover:bg-slate-700/20">
                  <td className="py-2 text-slate-400">{formatDate(tx.date)}</td>
                  <td className="py-2 text-slate-200">{tx.description}</td>
                  <td className="py-2">
                    <span className="px-2 py-0.5 rounded text-xs" style={{ backgroundColor: tx.categoryColor + '33', color: tx.categoryColor }}>
                      {tx.categoryName}
                    </span>
                  </td>
                  <td className="py-2 text-slate-400">{tx.bankName ?? '-'}</td>
                  <td className={`py-2 text-right font-medium ${tx.type === 'INCOME' ? 'text-emerald-400' : 'text-red-400'}`}>
                    {tx.type === 'INCOME' ? '+' : '-'}{formatCurrency(tx.amount)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  )
}
