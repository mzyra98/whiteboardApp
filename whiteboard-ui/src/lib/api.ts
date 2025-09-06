type BladApi = {
  timestamp?: string
  status?: number
  error?: string
  errorCode?: string
  code?: string
  message?: string
  detail?: string
  errors?: unknown
}

const API_BASE_URL: string = (import.meta.env.VITE_API_BASE_URL as string | undefined) ?? ""

function pobierzAccessToken(): string | null {
  return localStorage.getItem("accessToken")
}
function pobierzRefreshToken(): string | null {
  return localStorage.getItem("refreshToken")
}
function zapiszTokeny(accessToken: string, refreshToken: string) {
  localStorage.setItem("accessToken", accessToken)
  localStorage.setItem("refreshToken", refreshToken)
}
function wyczyscTokeny() {
  localStorage.removeItem("accessToken")
  localStorage.removeItem("refreshToken")
}

function requireConfig() {
  if (!API_BASE_URL) {
    const e = new Error("Brak konfiguracji API (VITE_API_BASE_URL).") as Error & { status?: number; code?: string }
    e.status = 0
    e.code = "KONFIGURACJA"
    throw e
  }
}

function joinUrl(base: string, path: string) {
  if (base.endsWith("/") && path.startsWith("/")) return base + path.slice(1)
  if (!base.endsWith("/") && !path.startsWith("/")) return base + "/" + path
  return base + path
}
function safeJson(s: string): unknown {
  try { return JSON.parse(s) } catch { return undefined }
}
function mapError(status: number, payload?: unknown) {
  const b = (payload || {}) as BladApi
  const msg =
      status === 429
          ? "Za dużo prób. Spróbuj ponownie za chwilę."
          : b.message || (b as any).error || `Błąd żądania (${status})`
  const e = new Error(msg) as Error & { status: number; code?: string; detail?: string; errorCode?: string }
  e.status = status
  e.code = b.code || b.errorCode
  e.errorCode = b.errorCode
  e.detail = b.detail
  return e
}

async function sprobojOdswiezyc(): Promise<boolean> {
  const refreshToken = pobierzRefreshToken()
  if (!refreshToken) return false
  const res = await fetch(joinUrl(API_BASE_URL, "/api/auth/odswiez"), {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken }),
    credentials: "omit"
  })
  if (!res.ok) return false
  const data = await res.json()
  if (data?.accessToken && data?.refreshToken) {
    zapiszTokeny(data.accessToken, data.refreshToken)
    return true
  }
  return false
}

async function request<T>(method: string, path: string, body?: unknown, init?: RequestInit): Promise<T> {
  requireConfig()
  const url = joinUrl(API_BASE_URL, path)

  const makeInit = (token?: string): RequestInit => ({
    method,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init?.headers ?? {})
    },
    body: body === undefined ? undefined : JSON.stringify(body),
    credentials: "omit",
    ...init
  })

  let res: Response
  try {
    res = await fetch(url, makeInit(pobierzAccessToken() || undefined))
  } catch {
    const e = new Error("Brak połączenia z serwerem") as Error & { status?: number; code?: string }
    e.status = 0
    e.code = "SIEC"
    throw e
  }

  if (res.status === 401 && (await sprobojOdswiezyc())) {
    res = await fetch(url, makeInit(pobierzAccessToken() || undefined))
  }

  if (res.status === 204) return undefined as unknown as T

  const text = await res.text()
  const json = text ? safeJson(text) : undefined

  if (res.ok) return (json as T) ?? (undefined as unknown as T)

  if (res.status === 401) wyczyscTokeny()
  throw mapError(res.status, json)
}

export const get = <T>(path: string, init?: RequestInit) => request<T>("GET", path, undefined, init)
export const post = <T>(path: string, body?: unknown, init?: RequestInit) => request<T>("POST", path, body, init)
export const put  = <T>(path: string, body?: unknown, init?: RequestInit) => request<T>("PUT", path, body, init)
export const del  = <T>(path: string, init?: RequestInit) => request<T>("DELETE", path, undefined, init)

export const getJson = get
export const postJson = post

export function zapiszTokenyJwt(accessToken: string, refreshToken: string) {
  zapiszTokeny(accessToken, refreshToken)
}
export function wylogujJwt() {
  wyczyscTokeny()
}

export const api = { get, post, put, del }
