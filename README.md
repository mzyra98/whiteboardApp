# WhiteboardApp — collaborative whiteboard (engineering thesis)

Monorepo z aplikacją tablicy do współpracy w czasie rzeczywistym.

- **Backend**: Spring Boot 3 (Java 21), JPA/Hibernate, MySQL, Flyway, Maven, JUnit 5
- **Frontend**: React 18 + TypeScript + Vite

---

## Spis treści

1. [Założenia i zakres](#zalozenia-i-zakres)
2. [Architektura i moduły](#architektura-i-moduly)
3. [Wymagania](#wymagania)
4. [Szybki start (dev)](#szybki-start-dev)
5. [Konfiguracja środowiska](#konfiguracja-srodowiska)
6. [Budowanie i uruchamianie](#budowanie-i-uruchamianie)
7. [Migracje bazy danych (Flyway)](#migracje-bazy-danych-flyway)
8. [Testy i jakość](#testy-i-jakosc)
9. [Najważniejsze API (przykłady)](#najwazniejsze-api-przyklady)
10. [Limitowanie zapytań (rate limiting)](#limitowanie-zapytan-rate-limiting)
11. [Struktura repozytorium](#struktura-repozytorium)
12. [Styl, konwencje i git](#styl-konwencje-i-git)
13. [Uwagi formalne (praca dyplomowa)](#uwagi-formalne-praca-dyplomowa)
14. [Licencja / prawa](#licencja-prawa)

---

<a id="zalozenia-i-zakres"></a>
## Założenia i zakres

Celem projektu jest przygotowanie działającej aplikacji „whiteboard” umożliwiającej współdzielenie tablicy oraz zarządzanie dostępami (linki udostępniania, prawa edycji/odczytu) z zachowaniem dobrych praktyk: migracji schematu bazy, testów, limitowania zapytań i spójnego API.

---

<a id="architektura-i-moduly"></a>
## Architektura i moduły

- **Warstwa prezentacji**: React + TypeScript (Vite), katalog `whiteboard-ui/`.
- **Warstwa usługowa (API)**: Spring Boot 3, pakiet `pl.tablica.wbapp.*`.
- **Baza danych**: MySQL 8.0 (domyślnie `whiteboard_db`), migracje przez **Flyway**.
- **Repozytoria**: Spring Data JPA.
- **Bezpieczeństwo**: nagłówki security Spring (frame-options, x-content-type-options); uproszczona identyfikacja nagłówkiem `X-User-Id` w wybranych endpointach (na potrzeby pracy).
- **Ochrona przed nadużyciami**: filtr + serwis limitujący częstość dołączeń po tokenie (`FiltrOgraniczaniaDolaczania`, `OgranicznikDolaczania`).

---

<a id="wymagania"></a>
## Wymagania

- **Java** 21 (Temurin/Oracle/OpenJDK)
- **Maven** 3.9+ (`mvnw`/`mvnw.cmd` w repo)
- **Node.js** 18+ oraz **npm**/**pnpm**/**yarn** (frontend)
- **MySQL** 8.0 działający lokalnie (`localhost:3306`)

---

<a id="szybki-start-dev"></a>
## Szybki start (dev)

1. Utwórz bazę danych:
   ```sql
   CREATE DATABASE whiteboard_db
     CHARACTER SET utf8mb4
     COLLATE utf8mb4_unicode_ci;
Skonfiguruj dostęp (login/hasło) w src/main/resources/application.properties.

Uruchom migracje + backend:

bash
Kopiuj
Edytuj
./mvnw clean verify
./mvnw spring-boot:run
Aplikacja nasłuchuje pod http://localhost:8080.

Frontend:

bash
Kopiuj
Edytuj
cd whiteboard-ui
npm ci          # lub: pnpm i / yarn
npm run dev
Dev-serwer Vite: http://localhost:5173.

<a id="konfiguracja-srodowiska"></a>

Konfiguracja środowiska
src/main/resources/application.properties (przykład):

properties
Kopiuj
Edytuj
spring.datasource.url=jdbc:mysql://localhost:3306/whiteboard_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=haslo

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

server.port=8080

logging.level.org.springframework.security=INFO
Uwaga: schemat bazy zarządza Flyway (validate). Nie używamy update ani create-drop w pracy dyplomowej.

<a id="budowanie-i-uruchamianie"></a>

Budowanie i uruchamianie
Backend

Budowa i testy:

bash
Kopiuj
Edytuj
./mvnw clean verify
Instalacja artefaktu do lokalnego repo:

bash
Kopiuj
Edytuj
./mvnw install
Uruchomienie:

bash
Kopiuj
Edytuj
./mvnw spring-boot:run
Klasa startowa: pl.tablica.wbapp.AplikacjaTablica.

Frontend

bash
Kopiuj
Edytuj
cd whiteboard-ui
npm ci
npm run dev        # tryb developerski
npm run build      # produkcyjny bundle
<a id="migracje-bazy-danych-flyway"></a>

Migracje bazy danych (Flyway)
Pliki migracji: src/main/resources/db/migration/ (konwencja V__opis.sql).

Walidacja stanu:

bash
Kopiuj
Edytuj
./mvnw -DskipTests flyway:validate
<a id="testy-i-jakosc"></a>

Testy i jakość
Testy jednostkowe/integracyjne: JUnit 5 (uruchamiane w mvn verify).

Krytyczne ścieżki: tworzenie użytkownika (/api/uzytkownicy), dołączanie po tokenie (/api/udostepnianie/dolacz), limitowanie (400/429).

Analiza statyczna: błędy kompilacji wyeliminowane; ostrzeżenia IDE ograniczone do minimum.

<a id="najwazniejsze-api-przyklady"></a>

Najważniejsze API (przykłady)
W przykładach stosujemy nagłówek X-User-Id (uproszczona identyfikacja na potrzeby pracy).

1) Rejestracja użytkownika

http
Kopiuj
Edytuj
POST /api/uzytkownicy
X-User-Id: 7
Content-Type: application/json

{
"nazwaWyswietlana": "Jan Kowalski",
"email": "jan.kowalski@example.com",
"haslo": "haslo123",
"rola": "UCZEN"
}
Odpowiedź: 200 OK.

2) Dołączanie do tablicy po tokenie (rate-limited)

http
Kopiuj
Edytuj
POST /api/udostepnianie/dolacz
X-User-Id: 25
Content-Type: application/json

{ "token": "00000000-0000-0000-0000-000000000000" }
Możliwe kody:

400 Bad Request – nieprawidłowy/nieistniejący token

429 Too Many Requests – przekroczony limit prób

200 OK – dołączenie zakończone powodzeniem (zwracane tablicaId i uprawnienie)

<a id="limitowanie-zapytan-rate-limiting"></a>

Limitowanie zapytań (rate limiting)
Za ochronę odpowiada duet:

FiltrOgraniczaniaDolaczania – OncePerRequestFilter na ścieżce /api/udostepnianie/dolacz

OgranicznikDolaczania – współdzielony serwis z oknem czasowym per IP+token

Parametry (tryb produkcyjny w kodzie):

MAKS_PROB_W_OKNIE = 10 prób

OKNO_MS = 60_000 ms

Przekroczenie limitu → wyjątek ZbytWieleProb i odpowiedź HTTP 429 (JSON).

<a id="struktura-repozytorium"></a>

Struktura repozytorium
bash
Kopiuj
Edytuj
whiteboardApp/
├─ src/
│  ├─ main/java/pl/tablica/wbapp/...
│  ├─ main/resources/
│  │  └─ db/migration/         # skrypty Flyway
│  └─ test/java/pl/tablica/wbapp/...
├─ whiteboard-ui/               # frontend (React + TS + Vite)
│  ├─ src/
│  └─ package.json
├─ pom.xml
└─ README.md
<a id="styl-konwencje-i-git"></a>

Styl, konwencje i git
Java: klasy/pakiety zgodnie z konwencją; DTO w dto, kontrolery w kontroler, usługi w usluga, wyjątki w wyjatek.

Commity: krótki imperatyw po angielsku (np. feat: ..., fix: ..., chore: ...).

.gitignore: bez artefaktów buildów (/target, whiteboard-ui/dist, .idea, itp.).

Branch: gałąź domyślna main; na GitHub włączone ograniczenia (pull request, blokada force-push).

<a id="uwagi-formalne-praca-dyplomowa"></a>

Uwagi formalne (praca dyplomowa)
Repozytorium jest prywatne (wymóg formalny). Do pracy pisemnej dołączono zrzuty ekranów i logi z Maven/Spring.
README obejmuje: cel, architekturę, technologie, proces budowania, konfigurację DB oraz kluczowe API — tak by recenzent mógł odtworzyć środowisko i zweryfikować ścieżki (w tym odpowiedź 429).

<a id="licencja-prawa"></a>

Licencja / prawa
Kod i materiały stanowią element pracy inżynierskiej.
Udostępnianie osobom trzecim wyłącznie za zgodą autora/opiekuna pracy.

markdown
Kopiuj
Edytuj
