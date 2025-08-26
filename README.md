# WhiteboardApp — collaborative whiteboard (praca inżynierska)

[![backend](https://github.com/mzyra98/whiteboardApp/actions/workflows/backend.yml/badge.svg)](https://github.com/mzyra98/whiteboardApp/actions/workflows/backend.yml)
[![frontend](https://github.com/mzyra98/whiteboardApp/actions/workflows/frontend.yml/badge.svg)](https://github.com/mzyra98/whiteboardApp/actions/workflows/frontend.yml)

Monorepo aplikacji tablicy do współpracy w czasie rzeczywistym.

- **Backend:** Spring Boot 3 (Java 21), JPA/Hibernate, MySQL, Flyway, Maven, JUnit 5
- **Frontend:** React 18 + TypeScript + Vite

---

## Spis treści

1. [Założenia i zakres](#toc-zalozenia-zakres)
2. [Architektura i moduły](#toc-architektura-moduly)
3. [Wymagania](#toc-wymagania)
4. [Szybki start (dev)](#toc-szybki-start)
5. [Konfiguracja środowiska](#toc-konfiguracja)
6. [Budowanie i uruchamianie](#toc-budowanie-uruchamianie)
7. [Migracje bazy danych (Flyway)](#toc-flyway)
8. [Testy i jakość](#toc-testy-jakosc)
9. [Najważniejsze API (przykłady)](#toc-api)
10. [Limitowanie zapytań (rate limiting)](#toc-rate-limiting)
11. [Struktura repozytorium](#toc-struktura)
12. [Styl, konwencje i git](#toc-styl-git)
13. [Uwagi formalne (praca dyplomowa)](#toc-uwagi-formalne)
14. [Licencja / prawa](#toc-licencja)

---

## <a id="toc-zalozenia-zakres"></a> Założenia i zakres

Celem jest działająca aplikacja „whiteboard” z mechanizmami udostępniania tablic (linki, uprawnienia), statystykami oraz ochroną (limit prób, walidacja), wraz z powtarzalną konfiguracją środowiska, migracjami bazy i CI dla backendu i frontendu.

---

## <a id="toc-architektura-moduly"></a> Architektura i moduły

- **Warstwa prezentacji:** React + TypeScript (Vite), katalog `whiteboard-ui/`.
- **Warstwa usługowa (REST API):** Spring Boot 3, pakiety `pl.tablica.wbapp.*`.
- **Baza danych:** MySQL 8, migracje przez **Flyway** (`src/main/resources/db/migration`).
- **Repozytoria:** Spring Data JPA.
- **Autoryzacja (na potrzeby pracy):** nagłówek `X-User-Id` (liczba całkowita).
- **CORS:** globalna konfiguracja dla `/api/**` z originami `http://localhost:*`.
- **Ochrona przed nadużyciami:** filtr i serwis limitujący dołączanie po tokenie.

---

## <a id="toc-wymagania"></a> Wymagania

- **Java** 21 (Temurin/Oracle/OpenJDK)
- **Maven** 3.9+ (wrappery `mvnw`/`mvnw.cmd`)
- **MySQL** 8.0 (lokalnie `localhost:3306`)
- **Node.js** 18+ oraz **npm/pnpm/yarn** (frontend)

---

## <a id="toc-szybki-start"></a> Szybki start (dev)

1. **Utwórz bazę danych:**
   ```sql
   CREATE DATABASE whiteboard_db
     CHARACTER SET utf8mb4
     COLLATE utf8mb4_unicode_ci;
2. **Backend — migracje + uruchomienie:**

./mvnw clean verify
./mvnw spring-boot:run


Aplikacja: http://localhost:8080 
1. **Frontend — dev serwer Vite:**

cd whiteboard-ui
npm ci
npm run dev


Aplikacja: http://localhost:5173
(lub kolejny wolny port)
<a id="toc-konfiguracja"></a> Konfiguracja środowiska

src/main/resources/application.properties (przykład):
spring.datasource.url=jdbc:mysql://localhost:3306/whiteboard_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=haslo

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

server.port=8080

Frontend — .env.local:
VITE_API_BASE_URL=http://localhost:8080
VITE_USER_ID_DO_TESTOW=9
<a id="toc-budowanie-uruchamianie"></a> Budowanie i uruchamianie
Backend

Budowa i testy:

./mvnw clean verify


Instalacja artefaktu do lokalnego repo:

./mvnw install


Uruchomienie:

./mvnw spring-boot:run

## Instrukcja uruchomienia (skrót)

**Backend**

./mvnw -B -q test
./mvnw -DskipTests package
java -jar target/*.jar

Klasa startowa: pl.tablica.wbapp.AplikacjaTablica.

**Frontend**
cd whiteboard-ui
npm ci
npm run dev        # tryb developerski

npm run build      # produkcyjny bundle

npm run preview    # podgląd buildu


(np. http://localhost:4173)

<a id="toc-flyway"></a> Migracje bazy danych (Flyway)

Pliki migracji: src/main/resources/db/migration/ (V__opis.sql).

Walidacja stanu:

./mvnw -DskipTests flyway:validate

<a id="toc-testy-jakosc"></a> Testy i jakość

Testy jednostkowe/integracyjne: JUnit 5 (uruchamiane w mvn verify).

Krytyczne ścieżki: dołączanie po tokenie (POST /api/udostepnianie/dolacz), tworzenie/anulowanie linków, 429.

Analiza statyczna: błędy kompilacji wyeliminowane; ostrzeżenia IDE ograniczone.

<a id="toc-api"></a> Najważniejsze API (przykłady)

Wszystkie wywołania z nagłówkiem:

X-User-Id: <liczba>


1) Dołączanie do tablicy

POST /api/udostepnianie/dolacz
Content-Type: application/json

{ "token": "..." }


200 → { "tablicaId": number, "uprawnienie": "PODGLAD"|"EDYCJA"|... }
400/404/429 → błąd w formacie globalnego handlera:

{
"timestamp": "...",
"status": 429,
"error": "Too Many Requests",
"code": "ZA_DUZO_ZADAN",
"message": "Za dużo prób. Spróbuj ponownie za chwilę."
}


1) Utworzenie linku udostępnienia

POST /api/tablice/{tablicaId}/udostepnij
Content-Type: application/json

{ "czasWMinutach": 60, "maksOsob": 5, "uprawnienie": "EDYCJA" }


201 → { "token": "...", "url": "...", "wygasa": "ISO-8601", "pozostaloWejsc": 5 }

1) Lista aktywnych linków

GET /api/tablice/{tablicaId}/udostepnienia


1) Anulowanie linku

DELETE /api/udostepnianie/anuluj/{token}


204 (no content)

1) Statystyki tablicy

GET /api/statystyki/tablice/{id}


200 → JSON (prezentowany jako „pretty JSON” w UI).

<a id="toc-rate-limiting"></a> Limitowanie zapytań (rate limiting)

Filtr FiltrOgraniczaniaDolaczania (OncePerRequestFilter) chroni POST /api/udostepnianie/dolacz.

Serwis OgranicznikDolaczania liczy próby w oknie czasowym per IP+token.

Parametry (wartości w kodzie):

MAKS_PROB_W_OKNIE = 10 prób

OKNO_MS = 60000 ms
Przekroczenie → wyjątek i odpowiedź HTTP 429.
<a id="toc-styl-git"></a> Styl, konwencje i git

Java: DTO w dto, kontrolery w kontroler, usługi w usluga, wyjątki w wyjatek.

Commity: krótki imperatyw (np. feat: ..., fix: ..., chore: ...).

.gitignore: bez artefaktów buildów (/target, whiteboard-ui/dist, .idea, itp.).

Branch: domyślna main; GitHub Actions dla backendu i frontendu.

<a id="toc-uwagi-formalne"></a> Uwagi formalne (praca dyplomowa)

Repozytorium zawiera kompletną konfigurację buildów i CI.
Instrukcja odtworzenia środowiska, migracje oraz przykłady kluczowych endpointów pozwalają na weryfikację działania (w tym ścieżek 400/404/429). Repo może być prywatne zgodnie z wymaganiami uczelni.

<a id="toc-licencja"></a> Licencja / prawa

Kod stanowi element pracy inżynierskiej autora.
Udostępnianie osobom trzecim wyłącznie za zgodą autora/opiekuna pracy.