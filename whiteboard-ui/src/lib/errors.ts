export const KOMUNIKATY_BLEDOW: Record<string, string> = {
    EMAIL_ZAJETY: "Adres e-mail jest już używany.",
    ZA_DUZO_ZADAN: "Zbyt wiele prób. Spróbuj ponownie później.",
    NIEPRAWIDLOWE_CREDENCJALE: "Nieprawidłowy e-mail lub hasło.",
    NIEPOPRAWNE_DANE_WEJSCIOWE: "Niepoprawne dane wejściowe.",
    BLEDY_WALIDACJI: "Wystąpiły błędy walidacji.",
    BRAK_UPRAWNIEN: "Brak uprawnień do wykonania operacji.",
    NIE_ZNALEZIONO_REKORDU: "Nie znaleziono żądanego zasobu.",
    WEWNETRZNY_BLAD: "Wystąpił błąd po stronie serwera."
};

export function komunikatBledu(code?: string, fallback?: string) {
    if (!code) return fallback ?? "Wystąpił błąd.";
    return KOMUNIKATY_BLEDOW[code] ?? (fallback ?? "Wystąpił błąd.");
}