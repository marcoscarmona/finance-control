'use client'
import { useEffect, useState } from 'react'
import { getDemoUserId, getBanks, getCreditCards } from '@/lib/api'
import { Bank, CreditCard } from '@/types'
import { Card } from '@/components/ui/Card'
import { formatCurrency } from '@/lib/utils'
import { Landmark, CreditCard as CardIcon, Loader2 } from 'lucide-react'

const bankTypeLabel: Record<string, string> = { BANK: 'Banco', DIGITAL_WALLET: 'Carteira Digital', CASH: 'Dinheiro' }

export default function BanksPage() {
  const [banks, setBanks] = useState<Bank[]>([])
  const [cards, setCards] = useState<CreditCard[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getDemoUserId().then(uid => Promise.all([getBanks(uid), getCreditCards(uid)]))
      .then(([bks, cds]) => { setBanks(bks); setCards(cds) })
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="flex justify-center py-20"><Loader2 className="animate-spin text-sky-400" size={32} /></div>

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Bancos & Cartões</h1>
        <p className="text-slate-400 text-sm">Suas instituições financeiras</p>
      </div>

      <Card title="Bancos">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {banks.map(b => (
            <div key={b.id} className="flex items-center gap-4 p-4 bg-slate-700/40 rounded-lg border border-slate-700">
              <div className="w-10 h-10 rounded-full bg-sky-500/20 flex items-center justify-center">
                <Landmark size={18} className="text-sky-400" />
              </div>
              <div>
                <p className="text-white font-medium">{b.name}</p>
                <p className="text-slate-400 text-sm">{bankTypeLabel[b.type]} {b.code ? `• Código ${b.code}` : ''}</p>
              </div>
            </div>
          ))}
        </div>
      </Card>

      <Card title="Cartões de Crédito">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {cards.map(c => {
            const bank = banks.find(b => b.id === c.bankId)
            return (
              <div key={c.id} className="p-4 bg-gradient-to-br from-slate-700 to-slate-800 rounded-xl border border-slate-600">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <p className="text-white font-semibold">{c.name}</p>
                    <p className="text-slate-400 text-sm">{bank?.name}</p>
                  </div>
                  <CardIcon size={20} className="text-sky-400" />
                </div>
                <p className="text-slate-300 text-sm font-mono mb-3">•••• •••• •••• {c.lastFourDigits}</p>
                <div className="grid grid-cols-3 gap-2 text-xs">
                  <div>
                    <p className="text-slate-500">Limite</p>
                    <p className="text-white font-medium">{formatCurrency(c.limitAmount)}</p>
                  </div>
                  <div>
                    <p className="text-slate-500">Fechamento</p>
                    <p className="text-white font-medium">Dia {c.closingDay}</p>
                  </div>
                  <div>
                    <p className="text-slate-500">Vencimento</p>
                    <p className="text-white font-medium">Dia {c.dueDay}</p>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </Card>
    </div>
  )
}
