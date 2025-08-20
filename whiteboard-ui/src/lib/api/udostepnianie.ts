import { api } from '../api'

export type Uprawnienie = 'PODGLAD' | 'EDYCJA' | string

export type DolaczDoTablicyRequest = {
    token: string
}

export type DolaczDoTablicyResponse = {
    tablicaId: number
    uprawnienie: Uprawnienie
}

export type UtworzLinkRequest = {
    czasWMinutach?: number
    maksOsob?: number
    uprawnienie?: Extract<Uprawnienie, 'PODGLAD' | 'EDYCJA'>
}

export type UtworzLinkResponse = {
    token: string
    url: string
    wygasa: string
    pozostaloWejsc: number | null
}

export type LinkUdostepnienia = {
    token: string
    wygasa: string
    maksWejsc?: number | null
    liczbaWejsc?: number | null
    uprawnienie: Uprawnienie
    anulowany: boolean
}

export function dolacz(req: DolaczDoTablicyRequest) {
    return api.post<DolaczDoTablicyResponse>('/api/udostepnianie/dolacz', req)
}

export function utworzLink(tablicaId: number, req: UtworzLinkRequest) {
    return api.post<UtworzLinkResponse>(`/api/tablice/${tablicaId}/udostepnij`, req)
}

export function listaLinkow(tablicaId: number) {
    return api.get<LinkUdostepnienia[]>(`/api/tablice/${tablicaId}/udostepnienia`)
}

export function anulujLink(token: string) {
    return api.del<void>(`/api/udostepnianie/anuluj/${token}`)
}