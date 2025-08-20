import { useState } from 'react'
import { dolacz } from '../lib/api/udostepnianie'
import type { DolaczDoTablicyResponse } from '../lib/api/udostepnianie'
import * as React from "react";

export default function Dolacz() {
    const [token, setToken] = useState('')
    const [wynik, setWynik] = useState<DolaczDoTablicyResponse | null>(null)
    const [blad, setBlad] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)

    async function wyslij(e: React.FormEvent) {
        e.preventDefault()
        setWynik(null)
        setBlad(null)
        const t = token.trim()
        if (!t) { setBlad('Podaj token'); return }
        setLoading(true)
        try {
            const r = await dolacz({ token: t })
            setWynik(r)
        } catch (e: any) {
            setBlad(e?.message || 'Błąd')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div>
            <h2>Dołącz do tablicy</h2>
            <form className="formularz" onSubmit={wyslij}>
                <div className="pole">
                    <label>Token</label>
                    <input value={token} onChange={e => setToken(e.target.value)} placeholder="wklej token" />
                </div>
                <div className="przyciski">
                    <button type="submit" disabled={loading}>{loading ? 'Łączenie…' : 'Dołącz'}</button>
                </div>
            </form>

            {blad && <p className="komunikat-blad">{blad}</p>}
            {wynik && (
                <div className="kod" style={{ marginTop: 12 }}>
                    tablicaId: {wynik.tablicaId}
                    {'\n'}
                    uprawnienie: {wynik.uprawnienie}
                </div>
            )}
        </div>
    )
}