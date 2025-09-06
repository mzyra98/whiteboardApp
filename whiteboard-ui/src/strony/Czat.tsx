import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { polaczCzat } from "../lib/czat";
import type { WiadomoscCzatu } from "../lib/czat";
import { pobierzAccessToken } from "../lib/autoryzacja";
import type { ReactElement } from "react";

export default function Czat(): ReactElement {
    const { id } = useParams<{ id: string }>();
    const tablicaId: number = Number(id ?? "0");
    const [wiadomosci, setWiadomosci] = useState<WiadomoscCzatu[]>([]);
    const [tresc, setTresc] = useState("");
    const connRef = useRef<{ wyslij: (t: string) => void; zamknij: () => void } | null>(null);

    useEffect(() => {
        const conn = polaczCzat(
            tablicaId,
            () => pobierzAccessToken() ?? undefined,
            (m) => setWiadomosci((prev) => [...prev, m])
        );
        connRef.current = conn;
        return () => conn.zamknij();
    }, [tablicaId]);

    function wyslij(e: React.FormEvent) {
        e.preventDefault();
        if (!tresc.trim()) return;
        connRef.current?.wyslij(tresc.trim());
        setTresc("");
    }

    return (
        <div className="p-4 max-w-2xl mx-auto">
            <h1 className="text-xl mb-3">Czat tablicy #{tablicaId}</h1>
            <div className="border rounded p-3 h-64 overflow-y-auto mb-3">
                {wiadomosci.map((m, i) => (
                    <div key={i} className="text-sm mb-1">
                        <span className="opacity-60 mr-2">{new Date(m.czas).toLocaleTimeString()}</span>
                        <span className="font-medium mr-2">#{m.autorId}</span>
                        <span>{m.tresc}</span>
                    </div>
                ))}
            </div>
            <form onSubmit={wyslij} className="flex gap-2">
                <input
                    className="flex-1 border rounded px-3 py-2"
                    value={tresc}
                    onChange={(e) => setTresc(e.target.value)}
                    placeholder="Napisz wiadomość…"
                />
                <button className="border rounded px-4 py-2">Wyślij</button>
            </form>
        </div>
    );
}