import { getJson, postJson } from "./api"

export type BiezacyUzytkownik = {
    id: number
    email: string
    nazwaWyswietlana: string
    rola: "ADMIN" | "NAUCZYCIEL" | "UCZEN"
}

type OdpowiedzLogowania = {
    accessToken: string
    refreshToken: string
}

export function zapiszTokeny(tokens: OdpowiedzLogowania) {
    localStorage.setItem("accessToken", tokens.accessToken)
    localStorage.setItem("refreshToken", tokens.refreshToken)
}

export function pobierzAccessToken(): string | null {
    return localStorage.getItem("accessToken")
}

export function pobierzRefreshToken(): string | null {
    return localStorage.getItem("refreshToken")
}

export function wyloguj() {
    localStorage.removeItem("accessToken")
    localStorage.removeItem("refreshToken")
}

export async function zaloguj(email: string, haslo: string): Promise<OdpowiedzLogowania> {
    const res = await postJson<OdpowiedzLogowania>("/api/auth/login", { email, haslo })
    zapiszTokeny(res)
    return res
}

export async function me(): Promise<BiezacyUzytkownik> {
    return getJson<BiezacyUzytkownik>("/api/auth/me")
}

export async function odswiez(): Promise<OdpowiedzLogowania> {
    const refreshToken = pobierzRefreshToken()
    if (!refreshToken) throw new Error("Brak refreshToken")
    const res = await postJson<OdpowiedzLogowania>("/api/auth/odswiez", { refreshToken })
    zapiszTokeny(res)
    return res
}

export function czyZalogowany(): boolean {
    return !!pobierzAccessToken()
}
