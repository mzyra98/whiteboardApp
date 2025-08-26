import { useState } from 'react'
import { dolacz } from '../lib/api/udostepnianie'
import * as React from 'react'

type DolaczOdp = { tablicaId: number; uprawnienie: string }

export default function Dolacz() {
  const [token, setToken] = useState('')
  const [wynik, setWynik] = useState<DolaczOdp | null>(null)
  const [blad, setBlad] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function wyslij(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setBlad(null)
    setWynik(null)

    if (!token.trim()) {
      setBlad('Wpisz token')
      return
    }

    setLoading(true)
    try {
      const res = (await dolacz({ token })) as DolaczOdp
      setWynik(res)
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Wystąpił błąd'
      setBlad(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="panel">
      <h2>Dołącz do tablicy</h2>
      <form onSubmit={wyslij} className="form">
        <label>
          Token
          <input
            type="text"
            value={token}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setToken(e.target.value)}
            placeholder="wklej token"
          />
        </label>
        <button type="submit" disabled={loading}>
          {loading ? 'Ładowanie…' : 'Dołącz'}
        </button>
      </form>

      {blad && <p className="blad">{blad}</p>}

      {wynik && (
        <pre className="wynik">{`tablicaId: ${wynik.tablicaId}
uprawnienie: ${wynik.uprawnienie}`}</pre>
      )}
    </div>
  )
}
