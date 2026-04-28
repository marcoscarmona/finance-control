interface CardProps {
  title?: string
  children: React.ReactNode
  className?: string
}

export function Card({ title, children, className = '' }: CardProps) {
  return (
    <div className={`bg-slate-800 border border-slate-700 rounded-xl p-5 ${className}`}>
      {title && <h3 className="text-slate-400 text-sm font-medium mb-4">{title}</h3>}
      {children}
    </div>
  )
}

interface StatCardProps {
  label: string
  value: string
  sub?: string
  color?: string
  icon?: React.ReactNode
}

export function StatCard({ label, value, sub, color = 'text-white', icon }: StatCardProps) {
  return (
    <div className="bg-slate-800 border border-slate-700 rounded-xl p-5">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-slate-400 text-sm mb-1">{label}</p>
          <p className={`text-2xl font-bold ${color}`}>{value}</p>
          {sub && <p className="text-slate-500 text-xs mt-1">{sub}</p>}
        </div>
        {icon && <div className="text-slate-500">{icon}</div>}
      </div>
    </div>
  )
}
