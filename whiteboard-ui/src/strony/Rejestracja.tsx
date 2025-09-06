import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../lib/api";

export default function Rejestracja() {
    const nav = useNavigate();
    const [displayName, setDisplayName] = useState("");
    const [login, setLogin] = useState("");
    const [haslo, setHaslo] = useState("");
    const [haslo2, setHaslo2] = useState("");
    const [blad, setBlad] = useState<string | null>(null);
    const [ok, setOk] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    async function zarejestruj(e: React.FormEvent) {
        e.preventDefault();
        setBlad(null);
        setOk(null);

        if (haslo !== haslo2) {
            setBlad("Hasła nie są takie same");
            return;
        }

        setLoading(true);
        try {
            await api.post("/api/auth/register", { displayName, login, haslo });
            setOk("Konto utworzone. Możesz się zalogować.");
            setTimeout(() => nav("/logowanie"), 800);
        } catch (err: any) {
            setBlad(err?.message ?? "Nie udało się utworzyć konta");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="auth">
            <div className="auth-card">
                <h1 className="auth-title">Utwórz konto</h1>

                <form onSubmit={zarejestruj} className="auth-form">
                    <label className="auth-label">Imię i nazwisko (widoczne)</label>
                    <input
                        className="auth-input"
                        value={displayName}
                        onChange={(e) => setDisplayName(e.target.value)}
                        placeholder="np. Jan Kowalski"
                        required
                    />

                    <label className="auth-label">Login / nazwa użytkownika</label>
                    <input
                        className="auth-input"
                        value={login}
                        onChange={(e) => setLogin(e.target.value)}
                        placeholder="np. jan.kowalski"
                        required
                    />

                    <label className="auth-label">Hasło</label>
                    <input
                        className="auth-input"
                        type="password"
                        value={haslo}
                        onChange={(e) => setHaslo(e.target.value)}
                        placeholder="minimum 8 znaków"
                        required
                    />

                    <label className="auth-label">Powtórz hasło</label>
                    <input
                        className="auth-input"
                        type="password"
                        value={haslo2}
                        onChange={(e) => setHaslo2(e.target.value)}
                        placeholder="powtórz hasło"
                        required
                    />

                    {blad && <div className="auth-error">{blad}</div>}
                    {ok && <div className="auth-ok">{ok}</div>}

                    <button className="auth-button" disabled={loading}>
                        {loading ? "Rejestruję..." : "Utwórz konto"}
                    </button>
                </form>

                <div className="auth-footer">
                    Masz już konto? <Link to="/logowanie">Zaloguj się</Link>
                </div>
            </div>
        </div>
    );
}
