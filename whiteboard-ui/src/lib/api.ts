type BladApi = {
    timestamp?: string
    status?: number
    error?: string
    code?: string
    message?: string
    detail?: string
    errors?: unknown
}

const baseUrl = (import.meta.env.VITE_API_BASE_URL as string | undefined) ?? ''
const userId = (import.meta.env.VITE_USER_ID_DO_TESTOW as string | undefined) ?? ''

function assertKonfiguracja() {
    if (!baseUrl || !userId) {
        const opis =
            'Brak konfiguracji API. Ustaw VITE_API_BASE_URL i VITE_USER_ID_DO_TESTOW w .env lub .env.local.'
        const err = new Error(opis) as Error & { status?: number; code?: string }
        err.status = 0
        err.code = 'KONFIGURACJA'
        throw err
    }
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
    assertKonfiguracja()
    const res = await fetch(normalizujUrl(baseUrl, path), {
        method,
        headers: {
            'Content-Type': 'application/json',
            'X-User-Id': String(userId)
        },
        body: body === undefined ? undefined : JSON.stringify(body)
    }).catch(() => {
        const e = new Error('Brak połączenia z serwerem') as Error & { status?: number; code?: string }
        e.status = 0
        e.code = 'SIEC'
        throw e
    })

    if (res.status === 204) return undefined as unknown as T
    const tekst = await res.text()
    const json = tekst ? bezpieczneJson(tekst) : undefined

    if (res.ok) return (json as T) ?? (undefined as unknown as T)
    throw mapujBlad(res.status, json)
}

function bezpieczneJson(s: string): unknown {
    try { return JSON.parse(s) } catch { return undefined }
}

function mapujBlad(status: number, payload?: unknown): Error & { status: number; code?: string; detail?: string } {
    const b = (payload || {}) as BladApi
    const err = new Error(
        status === 429
            ? 'Za dużo prób. Spróbuj ponownie za chwilę.'
            : b.message || b.error || 'Wystąpił błąd żądania'
    ) as Error & { status: number; code?: string; detail?: string }
    err.status = status
    err.code = b.code
    err.detail = b.detail
    return err
}

function normalizujUrl(b: string, p: string) {
    if (b.endsWith('/') && p.startsWith('/')) return b + p.slice(1)
    if (!b.endsWith('/') && !p.startsWith('/')) return b + '/' + p
    return b + p
}

export const api = {
    get: <T>(path: string) => request<T>('GET', path),
    post: <T>(path: string, body?: unknown) => request<T>('POST', path, body),
    del: <T>(path: string) => request<T>('DELETE', path)
}