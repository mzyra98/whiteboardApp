package pl.tablica.wbapp.kontroler;

public enum ErrorCode {
    BLEDNE_DANE_WEJSCIOWE("Błędne dane wejściowe"),
    NARUSZENIE_OGRANICZEN("Naruszenie ograniczeń"),
    BRAK_PARAMETRU("Brak wymaganego parametru"),
    ZLY_TYP_PARAMETRU("Zły typ parametru"),
    NIEDOZWOLONA_METODA("Niedozwolona metoda HTTP"),
    NIE_ZNALEZIONO("Nie znaleziono"),
    NIEPRAWIDLOWY_ARGUMENT("Nieprawidłowy argument"),
    WYGASL("Zasób wygasł"),
    ZABRONIONE_WYWOLANIE("Zabronione wywołanie"),
    ZA_DUZO_ZADAN("Za dużo żądań"),
    BLAD_WEWNETRZNY("Błąd wewnętrzny");

    public final String message;
    public final String code;

    ErrorCode(String message) {
        this.message = message;
        this.code = name();
    }
}