'use client'
import { useEffect, useState } from 'react'
import { getDemoUserId, getTransactions, deleteTransaction, createTransaction, getBanks, getCategories } from '@/lib/api'
import { Transaction, Bank, Category } from '@/types'
import { formatCurrency, formatDate, paymentMethodLabel } from '@/lib/utils'
import { Card } from '@/components/ui/Card'
import { Plus, Trash2, Loader2, X } from 'lucide-react'

const MONTHS = ['2026-04','2026-03','2026-02','2026-01']

export default function TransactionsPage() {
  const [userId, setUserId] = useState('')
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [banks, setBanks] = useState<Bank[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [yearMonth, setYearMonth] = useState('2026-04')
  const [bankFilter, setBankFilter] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({
    description: '', amount: '', date: '2026-04-25',
    type: 'EXPENSE', paymentMethod: 'PIX', categoryId: '', bankId: '',
    merchant: '', recurring: false,
  })

  useEffect(() => {
    getDemoUserId().then(uid => {
      setUserId(uid)
      return Promise.all([getTransactions(uid, yearMonth), getBanks(uid), getCategories(uid)])
    }).then(([txs, bks, cats]) => {
      setTransactions(txs)
      setBanks(bks)
      setCategories(cats)
    }).finally(() => setLoading(false))
  }, [])

  async function loadTransactions(uid: string, ym: string, bId: string) {
    setLoading(true)
    try {
      const txs = await getTransactions(uid, ym, bId || undefined)
      setTransactions(txs)
    } finally { setLoading(false) }
  }

  async function handleDelete(id: string) {
    if (!confirm('Excluir transação?')) return
    await deleteTransaction(userId, id)
    setTransactions(prev => prev.filter(t => t.id !== id))
  }

  async function handleCreate() {
    if (!form.categoryId || !form.amount) return
    const cat = categories.find(c => c.id === form.categoryId)
    await createTransaction(userId, {
      ...form,
      amount: parseFloat(form.amount),
      categoryId: form.categoryId,
      bankId: form.bankId || undefined,
      type: form.type as 'INCOME' | 'EXPENSE',
      paymentMethod: form.paymentMethod as any,
      date: form.date,
    })
    setShowForm(false)
    loadTransactions(userId, yearMonth, bankFilter)
  }

  if (loading && !userId) return <div className="flex justify-center py-20"><Loader2 className="animate-spin text-sky-400" size={32} /></div>

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Transações</h1>
          <p className="text-slate-400 text-sm">{transactions.length} transações encontradas</p>
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="flex items-center gap-2 bg-sky-500 hover:bg-sky-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        >
          <Plus size={16} /> Nova Transação
        </button>
      </div>

      <div className="flex gap-3">
        <select
          value={yearMonth}
          onChange={e => { setYearMonth(e.target.value); loadTransactions(userId, e.target.value, bankFilter) }}
          className="bg-slate-800 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm"
        >
          {MONTHS.map(m => <option key={m} value={m}>{m}</option>)}
        </select>
        <select
          value={bankFilter}
          onChange={e => { setBankFilter(e.target.value); loadTransactions(userId, yearMonth, e.target.value) }}
          className="bg-slate-800 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm"
        >
          <option value="">Todos os bancos</option>
          {banks.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
        </select>
      </div>

      <Card>
        {loading ? (
          <div className="flex justify-center py-10"><Loader2 className="animate-spin text-sky-400" size={24} /></div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-slate-500 border-b border-slate-700">
                  <th className="text-left py-2">Data</th>
                  <th className="text-left py-2">Descrição</th>
                  <th className="text-left py-2">Categoria</th>
                  <th className="text-left py-2">Banco</th>
                  <th className="text-left py-2">Pagamento</th>
                  <th className="text-right py-2">Valor</th>
                  <th className="py-2"></th>
                </tr>
              </thead>
              <tbody>
                {transactions.map(tx => (
                  <tr key={tx.id} className="border-b border-slate-700/50 hover:bg-slate-700/20">
                    <td className="py-2.5 text-slate-400">{formatDate(tx.date)}</td>
                    <td className="py-2.5 text-slate-200">{tx.description}</td>
                    <td className="py-2.5">
                      <span className="px-2 py-0.5 rounded text-xs" style={{ backgroundColor: (tx.categoryColor ?? '#888') + '33', color: tx.categoryColor ?? '#888' }}>
                        {tx.categoryName}
                      </span>
                    </td>
                    <td className="py-2.5 text-slate-400">{tx.bankName ?? '-'}</td>
                    <td className="py-2.5 text-slate-400">{paymentMethodLabel(tx.paymentMethod)}</td>
                    <td className={`py-2.5 text-right font-medium ${tx.type === 'INCOME' ? 'text-emerald-400' : 'text-red-400'}`}>
                      {tx.type === 'INCOME' ? '+' : '-'}{formatCurrency(tx.amount)}
                    </td>
                    <td className="py-2.5 pl-2">
                      <button onClick={() => handleDelete(tx.id)} className="text-slate-600 hover:text-red-400 transition-colors">
                        <Trash2 size={14} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {showForm && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-6 w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-white font-semibold">Nova Transação</h2>
              <button onClick={() => setShowForm(false)}><X size={18} className="text-slate-400" /></button>
            </div>
            <div className="space-y-3">
              {[
                { label: 'Descrição', key: 'description', type: 'text' },
                { label: 'Valor', key: 'amount', type: 'number' },
                { label: 'Data', key: 'date', type: 'date' },
                { label: 'Estabelecimento', key: 'merchant', type: 'text' },
              ].map(f => (
                <div key={f.key}>
                  <label className="text-slate-400 text-xs">{f.label}</label>
                  <input
                    type={f.type}
                    value={(form as any)[f.key]}
                    onChange={e => setForm(p => ({ ...p, [f.key]: e.target.value }))}
                    className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm mt-1 focus:outline-none focus:border-sky-500"
                  />
                </div>
              ))}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-slate-400 text-xs">Tipo</label>
                  <select value={form.type} onChange={e => setForm(p => ({ ...p, type: e.target.value }))} className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm mt-1">
                    <option value="EXPENSE">Despesa</option>
                    <option value="INCOME">Receita</option>
                  </select>
                </div>
                <div>
                  <label className="text-slate-400 text-xs">Pagamento</label>
                  <select value={form.paymentMethod} onChange={e => setForm(p => ({ ...p, paymentMethod: e.target.value }))} className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm mt-1">
                    {['CREDIT_CARD','DEBIT_CARD','PIX','CASH','BANK_TRANSFER','BOLETO'].map(m => (
                      <option key={m} value={m}>{paymentMethodLabel(m)}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div>
                <label className="text-slate-400 text-xs">Categoria</label>
                <select value={form.categoryId} onChange={e => setForm(p => ({ ...p, categoryId: e.target.value }))} className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm mt-1">
                  <option value="">Selecione...</option>
                  {categories.filter(c => c.type === form.type).map(c => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="text-slate-400 text-xs">Banco (opcional)</label>
                <select value={form.bankId} onChange={e => setForm(p => ({ ...p, bankId: e.target.value }))} className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm mt-1">
                  <option value="">Nenhum</option>
                  {banks.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
              </div>
            </div>
            <div className="flex gap-3 mt-5">
              <button onClick={() => setShowForm(false)} className="flex-1 bg-slate-700 hover:bg-slate-600 text-white rounded-lg py-2 text-sm">Cancelar</button>
              <button onClick={handleCreate} className="flex-1 bg-sky-500 hover:bg-sky-600 text-white rounded-lg py-2 text-sm font-medium">Salvar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
