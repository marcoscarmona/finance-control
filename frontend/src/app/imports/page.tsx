'use client'
import { useState } from 'react'
import { getDemoUserId, importCsv } from '@/lib/api'
import { Card } from '@/components/ui/Card'
import { Upload, CheckCircle, AlertCircle, FileText, Loader2 } from 'lucide-react'

export default function ImportsPage() {
  const [file, setFile] = useState<File | null>(null)
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState<{ imported: number; total: number } | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [dragging, setDragging] = useState(false)

  async function handleUpload() {
    if (!file) return
    setLoading(true)
    setError(null)
    setResult(null)
    try {
      const uid = await getDemoUserId()
      const res = await importCsv(uid, file)
      setResult(res)
    } catch (e: any) {
      setError(e?.response?.data?.message ?? e?.message ?? 'Erro ao importar arquivo')
    } finally {
      setLoading(false)
    }
  }

  function handleDrop(e: React.DragEvent) {
    e.preventDefault()
    setDragging(false)
    const f = e.dataTransfer.files[0]
    if (f?.name.endsWith('.csv')) setFile(f)
    else setError('Apenas arquivos CSV são aceitos')
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Importação</h1>
        <p className="text-slate-400 text-sm">Importe transações via arquivo CSV</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="Upload CSV">
          <div
            onDragOver={e => { e.preventDefault(); setDragging(true) }}
            onDragLeave={() => setDragging(false)}
            onDrop={handleDrop}
            className={`border-2 border-dashed rounded-xl p-8 text-center transition-colors ${
              dragging ? 'border-sky-400 bg-sky-400/5' : 'border-slate-600 hover:border-slate-500'
            }`}
          >
            {file ? (
              <div className="space-y-2">
                <FileText size={40} className="mx-auto text-sky-400" />
                <p className="text-white font-medium">{file.name}</p>
                <p className="text-slate-400 text-sm">{(file.size / 1024).toFixed(1)} KB</p>
                <button onClick={() => setFile(null)} className="text-slate-500 text-xs hover:text-red-400">Remover</button>
              </div>
            ) : (
              <>
                <Upload size={40} className="mx-auto text-slate-500 mb-3" />
                <p className="text-slate-300 mb-1">Arraste seu arquivo CSV aqui</p>
                <p className="text-slate-500 text-sm mb-4">ou clique para selecionar</p>
                <label className="cursor-pointer bg-slate-700 hover:bg-slate-600 text-white px-4 py-2 rounded-lg text-sm inline-block">
                  Selecionar arquivo
                  <input type="file" accept=".csv" className="hidden" onChange={e => setFile(e.target.files?.[0] ?? null)} />
                </label>
              </>
            )}
          </div>

          {file && (
            <button
              onClick={handleUpload}
              disabled={loading}
              className="w-full mt-4 bg-sky-500 hover:bg-sky-600 disabled:opacity-50 text-white py-2.5 rounded-lg font-medium flex items-center justify-center gap-2"
            >
              {loading ? <><Loader2 size={16} className="animate-spin" /> Importando...</> : <><Upload size={16} /> Importar</>}
            </button>
          )}

          {result && (
            <div className="mt-4 p-4 bg-emerald-500/10 border border-emerald-500/30 rounded-lg flex items-center gap-3">
              <CheckCircle size={20} className="text-emerald-400 flex-shrink-0" />
              <div>
                <p className="text-emerald-400 font-medium">Importação concluída!</p>
                <p className="text-slate-400 text-sm">{result.imported} de {result.total} transações importadas</p>
              </div>
            </div>
          )}

          {error && (
            <div className="mt-4 p-4 bg-red-500/10 border border-red-500/30 rounded-lg flex items-center gap-3">
              <AlertCircle size={20} className="text-red-400 flex-shrink-0" />
              <p className="text-red-400 text-sm">{error}</p>
            </div>
          )}
        </Card>

        <Card title="Formato Esperado">
          <p className="text-slate-400 text-sm mb-4">O arquivo CSV deve conter as seguintes colunas:</p>
          <div className="bg-slate-900 rounded-lg p-4 font-mono text-xs">
            <p className="text-sky-400">date,bank,description,category,amount,paymentMethod</p>
            <p className="text-slate-400 mt-2">2026-04-15,Nubank,Supermercado,Mercado,-350.00,DEBIT_CARD</p>
            <p className="text-slate-400">2026-04-16,C6 Bank,Uber,Transporte,-45.00,CREDIT_CARD</p>
            <p className="text-slate-400">2026-04-20,Nubank,Salário,Salário,8000.00,BANK_TRANSFER</p>
          </div>
          <div className="mt-4 space-y-2">
            {[
              ['date', 'Formato yyyy-MM-dd'],
              ['bank', 'Nome do banco cadastrado'],
              ['description', 'Descrição da transação'],
              ['category', 'Nome da categoria'],
              ['amount', 'Valor (negativo = despesa)'],
              ['paymentMethod', 'CREDIT_CARD, PIX, DEBIT_CARD, etc'],
            ].map(([col, desc]) => (
              <div key={col} className="flex gap-3 text-sm">
                <span className="text-sky-400 font-mono w-32 flex-shrink-0">{col}</span>
                <span className="text-slate-400">{desc}</span>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  )
}
