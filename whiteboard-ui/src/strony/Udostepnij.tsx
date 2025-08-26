import * as React from 'react'
import { useState } from 'react'
import { utworzLink, listaLinkow, anulujLink } from '../lib/api/udostepnianie'
import type { UtworzLinkRequest, LinkUdostepnienia, Uprawnienie } from '../lib/api/udostepnianie'

export default function Udostepnij() {
  const [tablicaId, setTablicaId] = useState<string>('11')
  const [czas, setCzas] = useState<string>('')
  const [limitOsob, setLimitOsob] = useState<string>('')
  const [uprawnienie, setUprawnienie] = useState<Uprawnienie | 'domyslne'>('domyslne')

  const [wynik, setWynik] = useState<string | null>(null)
  const [links, setLinks] = useState<LinkUdostepnienia[]>([])
  const [blad, setBlad] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  function buildRequest(): UtworzLinkRequest {
    const req: Partial<UtworzLinkRequest> = {}

    const min = Number(czas)
    const max = Number(limitOsob)
    if (Number.isFinite(min) && min > 0) req.czasWMinutach = min
    if (Number.isFinite(max) && max > 0) req.maksOsob = max

    const up: Uprawnienie | undefined =
      uprawnienie === 'EDYCJA' ? 'EDYCJA' : uprawnienie === 'PODGLAD' ? 'PODGLAD' : undefined

    if (up) {
      ;(req as { uprawnienie?: Uprawnienie }).uprawnienie = up
    }

    return req as UtworzLinkRequest
  }

  async function makeLink(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setBlad(null)
    setWynik(null)

    const idNum = Number(tablicaId)
    if (!Number.isInteger(idNum) || idNum <= 0) {
      setBlad('Podaj prawidłowe ID tablicy')
      return
    }

    setLoading(true)
    try {
      const body = buildRequest()
      const res = await utworzLink(idNum, body)
      setWynik(
        `token: ${res.token}\nurl: ${res.url}\nwygasa: ${res.wygasa}\npozostaloWejsc: ${
          res.pozostaloWejsc ?? 'n/d'
        }`
      )
      await zaladujListe(idNum)
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Wystąpił błąd'
      setBlad(msg)
    } finally {
      setLoading(false)
    }
  }

  async function zaladujListe(idNum: number) {
    try {
      const res = await listaLinkow(idNum)
      setLinks(res)
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Nie udało się pobrać listy'
      setBlad(msg)
    }
  }

  async function anuluj(token: string) {
    setBlad(null)
    try {
      await anulujLink(token)
      const idNum = Number(tablicaId)
      if (Number.isInteger(idNum) && idNum > 0) {
        await zaladujListe(idNum)
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Nie udało się anulować linku'
      setBlad(msg)
    }
  }

  return (
    <div className="panel">
      <h2>Udostępnij tablicę</h2>

      <form onSubmit={makeLink} className="form">
        <label>
          ID tablicy
          <input
            type="number"
            value={tablicaId}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setTablicaId(e.target.value)}
            placeholder="np. 1"
            min={1}
            step={1}
          />
        </label>

        <label>
          Czas w minutach (opcjonalnie)
          <input
            type="number"
            value={czas}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setCzas(e.target.value)}
            placeholder="np. 60"
            min={1}
            step={1}
          />
        </label>

        <label>
          Maksymalna liczba osób (opcjonalnie)
          <input
            type="number"
            value={limitOsob}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setLimitOsob(e.target.value)}
            placeholder="np. 5"
            min={1}
            step={1}
          />
        </label>

        <label>
          Uprawnienie (opcjonalnie)
          <select
            value={uprawnienie}
            onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
              setUprawnienie(e.target.value as 'domyslne' | Uprawnienie)
            }
          >
            <option value="domyslne">domyślne</option>
            <option value="EDYCJA">EDYCJA</option>
            <option value="PODGLAD">PODGLAD</option>
          </select>
        </label>

        <button type="submit" disabled={loading}>
          {loading ? 'Ładowanie…' : 'Utwórz link'}
        </button>
      </form>

      {blad && <p className="blad">{blad}</p>}
      {wynik && <pre className="wynik">{wynik}</pre>}

      <section>
        <h3>Aktywne linki</h3>
        <div className="lista">
          {links.length === 0 ? (
            <p>Podaj ID tablicy i utwórz lub pobierz listę.</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Token</th>
                  <th>Uprawnienie</th>
                  <th>Wygasa</th>
                  <th>Limit</th>
                  <th>Wejść</th>
                  <th>Status</th>
                  <th>Akcja</th>
                </tr>
              </thead>
              <tbody>
                {links.map((l) => (
                  <tr key={l.token}>
                    <td>{l.token}</td>
                    <td>{l.uprawnienie}</td>
                    <td>{l.wygasa}</td>
                    <td>{l.maksWejsc ?? '—'}</td>
                    <td>{l.liczbaWejsc ?? '—'}</td>
                    <td>{l.anulowany ? 'anulowany' : 'aktywny'}</td>
                    <td>
                      {!l.anulowany && (
                        <button onClick={() => anuluj(l.token)} type="button">
                          Anuluj
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </section>
    </div>
  )
}
