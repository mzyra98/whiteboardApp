import { api } from '../api'

export type StatystykiTablicy = {
  idTablicy: number
  liczbaPociagniec: number
  liczbaWspolpracownikow: number
  liczbaAktywnychLinkow: number
  ostatniaAktywnosc?: string | null
}

export async function pobierzStatystykiTablicy(id: number) {
  return api.get<StatystykiTablicy>(`/api/tablice/${id}/statystyki`)
}