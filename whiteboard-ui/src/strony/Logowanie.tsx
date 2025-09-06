import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { zaloguj } from "../lib/autoryzacja";
import { komunikatBledu } from "../lib/errors";

export default function Logowanie() {
    const nav = useNavigate();
    const [email, setEmail] = useState("admin@local");
    const [haslo, setHaslo] = useState("admin123");
    const [blad, setBlad] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setBlad(null);
        setLoading(true);
        try {
            await zaloguj(email.trim(), haslo);
            nav("/Udostepnij", { replace: true });
        } catch (err: any) {
            const code = err?.errorCode as string | undefined;
            setBlad(komunikatBledu(code, err?.message));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ maxWidth: 420, margin: "48px auto", padding: 16 }}>
            <h1>Logowanie</h1>
            <form onSubmit={onSubmit}>
                <div style={{ marginBottom: 12 }}>
                    <label>E-mail</label>
                    <input type="email" value={email} onChange={e => setEmail(e.target.value)} required style={{ width: "100%" }} />
                </div>
                <div style={{ marginBottom: 12 }}>
                    <label>Hasło</label>
                    <input type="password" value={haslo} onChange={e => setHaslo(e.target.value)} required style={{ width: "100%" }} />
                </div>
                {blad && <div style={{ color: "crimson", marginBottom: 12 }}>{blad}</div>}
                <button type="submit" disabled={loading}>{loading ? "Logowanie..." : "Zaloguj"}</button>
            </form>
        </div>
    );
}