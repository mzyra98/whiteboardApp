type BladApi = {
  timestamp?: string
  status?: number
  error?: string
  code?: string
  message?: string
  detail?: string
  errors?: unknown
}

const API_BASE_URL: string = (import.meta.env.VITE_API_BASE_URL as string | undefined) ?? ''

function getUserId(): string {
  const fromStorage = localStorage.getItem('userId') ?? localStorage.getItem('uid') ?? ''
  const fromEnv = (import.meta.env.VITE_USER_ID_DO_TESTOW as string | undefined) ?? ''
  return (fromStorage || fromEnv || '').toString()
}

function requireConfig() {
  if (!API_BASE_URL) {
    const e = new Error('Brak konfiguracji API (VITE_API_BASE_URL).') as Error & { status?: number; code?: string }
    e.status = 0
    e.code = 'KONFIGURACJA'
    throw e
  }
}

function requireUserId(uid: string) {
  if (!uid) {
    const e = new Error('Nie zalogowano. Ustaw ID użytkownika (Logowanie).') as Error & { status?: number; code?: string }
    e.status = 401
    e.code = 'BRAK_UZYTKOWNIKA'
    throw e
  }
}

function joinUrl(base: string, path: string) {
  if (base.endsWith('/') && path.startsWith('/')) return base + path.slice(1)
  if (!base.endsWith('/') && !path.startsWith('/')) return base + '/' + path
  return base + path
}

function safeJson(s: string): unknown {
  try { return JSON.parse(s) } catch { return undefined }
}

function mapError(status: number, payload?: unknown) {
  const b = (payload || {}) as BladApi
  const msg =
      status === 429
          ? 'Za dużo prób. Spróbuj ponownie za chwilę.'
          : b.message || b.error || `Błąd żądania (${status})`

  const e = new Error(msg) as Error & { status: number; code?: string; detail?: string }
  e.status = status
  e.code = b.code
  e.detail = b.detail
  return e
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  requireConfig()
  const uid = getUserId()
  requireUserId(uid)

  const url = joinUrl(API_BASE_URL, path)

  let res: Response
  try {
    res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': String(uid),
      },
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch {
    const e = new Error('Brak połączenia z serwerem') as Error & { status?: number; code?: string }
    e.status = 0
    e.code = 'SIEC'
    throw e
  }

  if (res.status === 204) return undefined as unknown as T

  const text = await res.text()
  const json = text ? safeJson(text) : undefined

  if (res.ok) return (json as T) ?? (undefined as unknown as T)

  throw mapError(res.status, json)
}
export const get = <T>(path: string) => request<T>('GET', path)
export const post = <T>(path: string, body?: unknown) => request<T>('POST', path, body)
export const put  = <T>(path: string, body?: unknown) => request<T>('PUT', path, body)
export const del  = <T>(path: string) => request<T>('DELETE', path)

export function setUserId(uid: number | string) {
  localStorage.setItem('userId', String(uid))
}

export function clearUserId() {
  localStorage.removeItem('userId')
}

export const api = {
  get,
  post,
  put,
  del,
}
