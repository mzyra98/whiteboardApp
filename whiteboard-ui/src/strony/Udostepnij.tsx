import { useEffect, useMemo, useState } from 'react'
import { anulujLink, listaLinkow, utworzLink } from '../lib/api/udostepnianie'
import type { LinkUdostepnienia, UtworzLinkRequest, UtworzLinkResponse } from '../lib/api/udostepnianie'
import * as React from "react";

type UprawChoice = '' | 'PODGLAD' | 'EDYCJA'

export default function Udostepnij() {
    const [tablicaId, setTablicaId] = useState('')
    const [czas, setCzas] = useState('')
    const [maks, setMaks] = useState('')
    const [uprawnienie, setUprawnienie] = useState<UprawChoice>('')
    const [blad, setBlad] = useState<string | null>(null)
    const [ok, setOk] = useState<UtworzLinkResponse | null>(null)
    const [lista, setLista] = useState<LinkUdostepnienia[] | null>(null)
    const [loading, setLoading] = useState(false)
    const [loadingLista, setLoadingLista] = useState(false)

    const idNum = useMemo(() => Number(tablicaId), [tablicaId])

    async function zaladujListe(id: number) {
        setLoadingLista(true)
        try {
            const r = await listaLinkow(id)
            setLista(r)
        } catch {
            setLista(null)
        } finally {
            setLoadingLista(false)
        }
    }

    useEffect(() => {
        const n = Number(tablicaId)
        if (Number.isInteger(n) && n > 0) void zaladujListe(n)
        else setLista(null)
    }, [tablicaId])

    async function wyslij(e: React.FormEvent) {
        e.preventDefault()
        setBlad(null)
        setOk(null)
        const id = Number(tablicaId)
        if (!Number.isInteger(id) || id <= 0) { setBlad('Podaj poprawne ID tablicy'); return }

        // @ts-ignore
        const req: UtworzLinkRequest = {
            ...(czas.trim() !== '' ? { czasWMinutach: Number(czas) } : {}),
            ...(maks.trim() !== '' ? { maksOsob: Number(maks) } : {}),
            ...(uprawnienie !== '' ? { uprawnienie } : {})
        }

        setLoading(true)
        try {
            const r = await utworzLink(id, req)
            setOk(r)
            await zaladujListe(id)
        } catch (e: any) {
            setBlad(e?.message || 'Błąd')
        } finally {
            setLoading(false)
        }
    }

    async function onAnuluj(t: string) {
        if (!Number.isInteger(idNum) || idNum <= 0) return
        try {
            await anulujLink(t)
            await zaladujListe(idNum)
        } catch {}
    }

    return (
        <div>
            <h2>Udostępnij tablicę</h2>
            <form className="formularz" onSubmit={wyslij}>
                <div className="pole">
                    <label>ID tablicy</label>
                    <input inputMode="numeric" value={tablicaId} onChange={e => setTablicaId(e.target.value)} placeholder="np. 1" />
                </div>
                <div className="pole">
                    <label>Czas w minutach (opcjonalnie)</label>
                    <input inputMode="numeric" value={czas} onChange={e => setCzas(e.target.value)} placeholder="np. 60" />
                </div>
                <div className="pole">
                    <label>Maksymalna liczba osób (opcjonalnie)</label>
                    <input inputMode="numeric" value={maks} onChange={e => setMaks(e.target.value)} placeholder="np. 5" />
                </div>
                <div className="pole">
                    <label>Uprawnienie (opcjonalnie)</label>
                    <select value={uprawnienie} onChange={e => setUprawnienie(e.target.value as UprawChoice)}>
                        <option value="">domyślne</option>
                        <option value="PODGLAD">PODGLAD</option>
                        <option value="EDYCJA">EDYCJA</option>
                    </select>
                </div>
                <div className="przyciski">
                    <button type="submit" disabled={loading}>{loading ? 'Tworzenie…' : 'Utwórz link'}</button>
                </div>
            </form>

            {blad && <p className="komunikat-blad">{blad}</p>}
            {ok && (
                <div className="kod" style={{ marginTop: 12 }}>
                    token: {ok.token}
                    {'\n'}
                    url: {ok.url}
                    {'\n'}
                    wygasa: {ok.wygasa}
                    {'\n'}
                    pozostaloWejsc: {ok.pozostaloWejsc === null ? 'brak limitu' : ok.pozostaloWejsc}
                </div>
            )}

            <h3 style={{ marginTop: 24 }}>Aktywne linki</h3>
            {!lista && <p>{loadingLista ? 'Ładowanie…' : 'Podaj ID tablicy'}</p>}
            {lista && (
                <table className="tabela">
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
                    {lista.map(l => (
                        <tr key={l.token}>
                            <td>{l.token}</td>
                            <td><span className="badge">{l.uprawnienie}</span></td>
                            <td>{l.wygasa}</td>
                            <td>{l.maksWejsc ?? '—'}</td>
                            <td>{l.liczbaWejsc ?? '—'}</td>
                            <td>{l.anulowany ? 'anulowany' : 'aktywny'}</td>
                            <td>
                                <button onClick={() => void onAnuluj(l.token)} disabled={l.anulowany}>Anuluj</button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    )
}
