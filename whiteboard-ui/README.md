# WhiteboardApp — collaborative whiteboard (praca inżynierska)

[![backend](https://github.com/mzyra98/whiteboardApp/actions/workflows/backend.yml/badge.svg)](https://github.com/mzyra98/whiteboardApp/actions/workflows/backend.yml)
[![frontend](https://github.com/mzyra98/whiteboardApp/actions/workflows/frontend.yml/badge.svg)](https://github.com/mzyra98/whiteboardApp/actions/workflows/frontend.yml)

# AplikacjaTablica — frontend (Vite + React 18 + TS)

## Wymagania
- Node.js 18+ i npm 9+
- Backend lokalnie: http://localhost:8080

## Konfiguracja środowiska
Utwórz `.env.local` na podstawie `.env.example`:
VITE_API_BASE_URL=http://localhost:8080
VITE_USER_ID_DO_TESTOW=9

shell
Kopiuj
Edytuj

## Instalacja
npm install

shell
Kopiuj
Edytuj

## Tryb deweloperski
npm run dev

bash
Kopiuj
Edytuj
Aplikacja: http://localhost:5173

## Build produkcyjny
npm run build

markdown
Kopiuj
Edytuj
Wynik w `dist/`.

## Podgląd buildu lokalnie
npm run preview

markdown
Kopiuj
Edytuj
Podgląd pod adresem wskazanym w konsoli.

## Warstwa HTTP
- Globalny nagłówek `X-User-Id` pobierany z `VITE_USER_ID_DO_TESTOW`.
- Obsługa błędów po polach `code`, `message`, `detail`. Dla 429 komunikat: „Za dużo prób. Spróbuj ponownie za chwilę.”

## Checklisty testów ręcznych UI

### Dołączanie
- Token pusty → walidacja na kliencie: „Podaj token”.
- Token nieistniejący → 404; komunikat z backendu wyświetlony w `message/detail`.
- Kilka szybkich prób → 429; jasny komunikat o limicie.
- Token poprawny → 200; widoczne `tablicaId` i `uprawnienie`.

### Udostępnianie
- Wpisz poprawne `ID tablicy` i utwórz link bez opcji → 201; pokazuje `token`, `url`, `wygasa`, `pozostaloWejsc`.
- Lista aktywnych linków zawiera nowy wpis.
- Ustaw opcjonalnie `czasWMinutach`, `maksOsob`, `uprawnienie` → 201; pola odwzorowane.
- Kliknij „Anuluj” przy wpisie → po 204 status zmienia się na „anulowany” i przycisk jest wyłączony.

### Statystyki
- Istniejąca tablica → pretty JSON.
- Nieistniejąca → 404; komunikat z backendu wyświetlony.

## Struktura kluczowych plików
src/
App.tsx
main.tsx
index.css
lib/
api.ts
api/
statystyki.ts
udostepnianie.ts
strony/
Dolacz.tsx
Statystyki.tsx
Udostepnij.tsx

Kopiuj
Edytuj
