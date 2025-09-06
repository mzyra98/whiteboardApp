import SockJS from "sockjs-client";
import type { IMessage } from "@stomp/stompjs";
import { Client, Stomp } from "@stomp/stompjs";

export type WiadomoscCzatu = {
    tablicaId: number;
    autorId: number;
    tresc: string;
    czas: string;
};

export function polaczCzat(
    tablicaId: number,
    pobierzToken?: () => string | undefined,
    onMsg?: (m: WiadomoscCzatu) => void
): { wyslij: (tresc: string) => void; zamknij: () => void } {
    const sock = new SockJS(import.meta.env.VITE_API_BASE_URL + "/ws");
    const client: Client = Stomp.over(sock as any);
    client.reconnectDelay = 0;

    client.beforeConnect = () => {
        if (pobierzToken) {
            const t = pobierzToken();
            if (t) client.connectHeaders = { Authorization: `Bearer ${t}` };
        }
    };

    client.onConnect = () => {
        client.subscribe(`/topic/tablica.${tablicaId}`, (frame: IMessage) => {
            try {
                const raw = JSON.parse(frame.body);
                const msg: WiadomoscCzatu = {
                    tablicaId: Number(raw.tablicaId ?? tablicaId),
                    autorId: Number(raw.autorId ?? 0),
                    tresc: String(raw.tresc ?? ""),
                    czas: String(raw.czas ?? new Date().toISOString()),
                };
                onMsg?.(msg);
            } catch {
            }
        });
    };

    client.activate();

    return {
        wyslij: (tresc: string) => {
            const body = JSON.stringify({ autorId: 0, tresc });
            client.publish({ destination: `/app/tablica.${tablicaId}`, body });
        },
        zamknij: () => client.deactivate(),
    };
}
