import * as React from 'react'
import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { setUserId, clearUserId } from '../lib/api'

export default function Logowanie() {
    const navigate = useNavigate()
    const [uid, setUid] = useState<string>(() => localStorage.getItem('userId') ?? '')

    useEffect(() => { document.title = 'Logowanie' }, [])

    function zapisz(e: React.FormEvent) {
        e.preventDefault()
        if (!uid.trim()) return
        setUserId(uid.trim())
        navigate('/udostepnij')
    }

    function wyloguj() {
        clearUserId()
        setUid('')
    }

    return (
        <div className="panel">
            <h2>Logowanie</h2>
            <form className="form" onSubmit={zapisz}>
                <label>
                    ID użytkownika
                    <input
                        type="number"
                        min={1}
                        step={1}
                        value={uid}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => setUid(e.target.value)}
                        placeholder="np. 7"
                    />
                </label>
                <button type="submit">Zaloguj</button>
                <button type="button" onClick={wyloguj}>Wyloguj</button>
            </form>
            <p className="komunikat-sukces" style={{marginTop:12}}>
                Właściciel testowej tablicy: <strong>7</strong>. Uczeń: <strong>9</strong>.
            </p>
        </div>
    )
}
