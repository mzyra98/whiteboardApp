import { useState } from 'react'
import { pobierzStatystykiTablicy } from '../lib/api/statystyki'
import * as React from "react";

export default function Statystyki() {
    const [id, setId] = useState('')
    const [json, setJson] = useState<string | null>(null)
    const [blad, setBlad] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)

    async function pobierz(e: React.FormEvent) {
        e.preventDefault()
        setJson(null)
        setBlad(null)
        const n = Number(id)
        if (!Number.isInteger(n) || n <= 0) { setBlad('Podaj poprawne ID tablicy'); return }
        setLoading(true)
        try {
            const data = await pobierzStatystykiTablicy(n)
            setJson(JSON.stringify(data, null, 2))
        } catch (e: any) {
            setBlad(e?.message || 'Błąd')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div>
            <h2>Statystyki tablicy</h2>
            <form className="formularz" onSubmit={pobierz}>
                <div className="pole">
                    <label>ID tablicy</label>
                    <input inputMode="numeric" value={id} onChange={e => setId(e.target.value)} placeholder="np. 1" />
                </div>
                <div className="przyciski">
                    <button type="submit" disabled={loading}>{loading ? 'Pobieranie…' : 'Pobierz'}</button>
                </div>
            </form>

            {blad && <p className="komunikat-blad">{blad}</p>}
            {json && <pre className="kod" style={{ marginTop: 12 }}>{json}</pre>}
        </div>
    )
}