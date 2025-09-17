package pl.tablica.wbapp.wyjatek;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    WEWNETRZNY_BLAD(HttpStatus.INTERNAL_SERVER_ERROR, "Wystąpił błąd wewnętrzny."),
    NIEPOPRAWNE_DANE_WEJSCIOWE(HttpStatus.BAD_REQUEST, "Niepoprawne dane wejściowe."),
    BLEDY_WALIDACJI(HttpStatus.BAD_REQUEST, "Wystąpiły błędy walidacji."),
    BRAK_UPRAWNIEN(HttpStatus.FORBIDDEN, "Brak uprawnień."),
    NIEDOZWOLONA_METODA(HttpStatus.METHOD_NOT_ALLOWED, "Niedozwolona metoda."),
    NIE_ZNALEZIONO_REKORDU(HttpStatus.NOT_FOUND, "Nie znaleziono."),
    EMAIL_ZAJETY(HttpStatus.CONFLICT, "Adres e-mail jest już używany."),
    NAZWA_UZYTKOWNIKA_ZAJETA(HttpStatus.CONFLICT, "Nazwa użytkownika jest już zajęta."),
    UZYTKOWNIK_MA_TABLICE(HttpStatus.CONFLICT, "Użytkownik ma już tablicę."),
    ZA_DUZO_ZADAN(HttpStatus.TOO_MANY_REQUESTS, "Za dużo żądań."),
    NIEPRAWIDLOWE_CREDENCJALE(HttpStatus.UNAUTHORIZED, "Nieprawidłowy e-mail lub hasło."),
    WALIDACJA(HttpStatus.BAD_REQUEST, "Błąd walidacji."),
    NIEPOPRAWNE_ZADANIE(HttpStatus.BAD_REQUEST, "Nieprawidłowe żądanie."),
    NIEUPOWAZNIONY(HttpStatus.UNAUTHORIZED, "Brak uwierzytelnienia.");

    private final HttpStatus httpStatus;
    private final String domyslnyKomunikat;

    ErrorCode(HttpStatus httpStatus, String domyslnyKomunikat) {
        this.httpStatus = httpStatus;
        this.domyslnyKomunikat = domyslnyKomunikat;
    }

    public HttpStatus getHttpStatus() { return httpStatus; }
    public String getDomyslnyKomunikat() { return domyslnyKomunikat; }
}
