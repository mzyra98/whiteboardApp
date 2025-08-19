package pl.tablica.wbapp.udostepnianie.usluga;

import org.junit.jupiter.api.Test;
import pl.tablica.wbapp.udostepnianie.wyjatek.ZbytWieleProb;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OgranicznikDolaczaniaTest {

    @Test
    void rzucaWyjatekPrzyJedenastejProbieTegoSamegoKlucza() {
        OgranicznikDolaczania limiter = new OgranicznikDolaczania(10, 5_000);
        String klucz = "127.0.0.1|tokenABC";

        for (int i = 0; i < 10; i++) {
            limiter.wymagajPozwolenia(klucz);
        }
        assertThrows(ZbytWieleProb.class, () -> limiter.wymagajPozwolenia(klucz));
    }

    @Test
    void niezalezneLicznikiDlaRoznychKluczy() {
        OgranicznikDolaczania limiter = new OgranicznikDolaczania(2, 5_000);
        String k1 = "10.0.0.1|A";
        String k2 = "10.0.0.2|A";

        limiter.wymagajPozwolenia(k1);
        limiter.wymagajPozwolenia(k1);
        assertThrows(ZbytWieleProb.class, () -> limiter.wymagajPozwolenia(k1));

        limiter.wymagajPozwolenia(k2);
        limiter.wymagajPozwolenia(k2);
        assertThrows(ZbytWieleProb.class, () -> limiter.wymagajPozwolenia(k2));
    }
}
