import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import type { BiezacyUzytkownik } from "../lib/autoryzacja";
import { me, wyloguj } from "../lib/autoryzacja";

export default function Konto() {
    const nav = useNavigate();
    const [uzytkownik, setUzytkownik] = useState<BiezacyUzytkownik | null>(null);
    const [blad, setBlad] = useState<string | null>(null);

    useEffect(() => {
        (async () => {
            try {
                const dane = await me();
                setUzytkownik(dane);
            } catch (err: any) {
                setBlad(err?.message ?? "Nie udało się pobrać danych konta.");
                wyloguj();
                nav("/Logowanie", { replace: true });
            }
        })();
    }, [nav]);

    function onWyloguj() {
        wyloguj();
        nav("/Logowanie", { replace: true });
    }

    if (!uzytkownik) {
        return <div style={{ padding: 16 }}>{blad ?? "Ładowanie…"}</div>;
    }

    return (
        <div style={{ maxWidth: 720, margin: "32px auto", padding: 16 }}>
            <h1>Moje konto</h1>
            <div style={{ lineHeight: 1.8 }}>
                <div><b>ID:</b> {uzytkownik.id}</div>
                <div><b>E-mail:</b> {uzytkownik.email}</div>
                <div><b>Nazwa wyświetlana:</b> {uzytkownik.nazwaWyswietlana}</div>
                <div><b>Rola:</b> {uzytkownik.rola}</div>
                <button style={{ marginTop: 16 }} onClick={onWyloguj}>Wyloguj</button>
            </div>
        </div>
    );
}