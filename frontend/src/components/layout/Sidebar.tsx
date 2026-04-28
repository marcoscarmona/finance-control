'use client'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { LayoutDashboard, ArrowLeftRight, CreditCard, PiggyBank, Upload, TrendingUp } from 'lucide-react'

const nav = [
  { href: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { href: '/transactions', icon: ArrowLeftRight, label: 'Transações' },
  { href: '/banks', icon: CreditCard, label: 'Bancos & Cartões' },
  { href: '/budgets', icon: PiggyBank, label: 'Orçamentos' },
  { href: '/imports', icon: Upload, label: 'Importação' },
]

export default function Sidebar() {
  const path = usePathname()
  return (
    <aside className="w-60 bg-slate-800 border-r border-slate-700 flex flex-col">
      <div className="p-6 border-b border-slate-700">
        <div className="flex items-center gap-2">
          <TrendingUp className="text-sky-400" size={22} />
          <span className="font-bold text-white text-lg">FinControl</span>
        </div>
        <p className="text-slate-400 text-xs mt-1">Marcos Carmona</p>
      </div>
      <nav className="flex-1 py-4">
        {nav.map(({ href, icon: Icon, label }) => {
          const active = path === href
          return (
            <Link
              key={href}
              href={href}
              className={`flex items-center gap-3 px-6 py-3 text-sm transition-colors ${
                active
                  ? 'bg-sky-500/10 text-sky-400 border-r-2 border-sky-400'
                  : 'text-slate-400 hover:text-white hover:bg-slate-700/50'
              }`}
            >
              <Icon size={18} />
              {label}
            </Link>
          )
        })}
      </nav>
      <div className="p-4 border-t border-slate-700">
        <p className="text-slate-500 text-xs">v1.0.0 • MVP</p>
      </div>
    </aside>
  )
}
