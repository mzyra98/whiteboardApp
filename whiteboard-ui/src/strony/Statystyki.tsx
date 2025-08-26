import { useState, type FormEvent, type ChangeEvent } from 'react'
import { pobierzStatystykiTablicy, type StatystykiTablicy } from '../lib/api/statystyki'

export default function Statystyki() {
  const [id, setId] = useState<string>('1')
  const [dane, setDane] = useState<StatystykiTablicy | null>(null)
  const [blad, setBlad] = useState<string | null>(null)
  const [loading, setLoading] = useState<boolean>(false)

  const onZmianaId = (e: ChangeEvent<HTMLInputElement>) => setId(e.target.value)

  async function pobierz(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setBlad(null)
    setDane(null)

    const idNum = Number(id)
    if (!Number.isInteger(idNum) || idNum <= 0) {
      setBlad('Podaj prawidłowe ID tablicy')
      return
    }

    setLoading(true)
    try {
      const data = await pobierzStatystykiTablicy(idNum)
      setDane(data)
    } catch (err: unknown) {
      if (err && typeof err === 'object') {
        const anyErr = err as Record<string, unknown>
        const code = typeof anyErr.code === 'string' ? anyErr.code : undefined
        const msg =
            typeof anyErr.message === 'string'
                ? anyErr.message
                : 'Wystąpił błąd podczas pobierania statystyk'
        setBlad(code ? `${code}: ${msg}` : msg)
      } else {
        setBlad('Wystąpił błąd podczas pobierania statystyk')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
      <div className="panel">
        <h2>Statystyki tablicy</h2>

        <form onSubmit={pobierz} className="form">
          <label htmlFor="stat-id">ID tablicy</label>
          <input
              id="stat-id"
              name="tablicaId"
              type="number"
              value={id}
              onChange={onZmianaId}
              placeholder="np. 1"
              min={1}
              step={1}
          />

          <button id="stat-submit" type="submit" disabled={loading}>
            {loading ? 'Ładowanie…' : 'Pobierz'}
          </button>
        </form>

        {blad && <p className="blad">{blad}</p>}

        {dane && (
            <pre className="json">{JSON.stringify(dane as unknown, null, 2)}</pre>
        )}
      </div>
  )
}