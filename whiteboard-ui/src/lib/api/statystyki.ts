import { api } from '../api'

export function pobierzStatystykiTablicy(id: number) {
    return api.get<Record<string, unknown>>(`/api/statystyki/tablice/${id}`)
}
