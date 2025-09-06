package pl.tablica.wbapp.usluga.realizacja;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.Tablica;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumTablicy;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class EksportPustejTablicyIT {

    @Autowired
    private UslugaImportuIEksportuDomyslneUstawienia usluga;

    @Autowired
    private RepozytoriumKontaUzytkownika kontoRepozytorium;

    @Autowired
    private RepozytoriumTablicy tablicaRepozytorium;

    private KontoUzytkownika wybierzWlasciciela() {
        return kontoRepozytorium.findByEmailIgnoreCase("nauczyciel@local")
                .orElseGet(() -> kontoRepozytorium.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Brak kont w bazie testowej")));
    }

    @Test
    void powinienEksportowacPustaTablice() {
        var wlasciciel = wybierzWlasciciela();

        var t = new Tablica();
        t.setTytul("Pusta IT");
        t.setWlasciciel(wlasciciel);
        t = tablicaRepozytorium.save(t);

        var dto = usluga.eksportuj(t.getId(), wlasciciel.getEmail());
        assertNotNull(dto);
        assertEquals("Pusta IT", dto.tytul());
        assertNotNull(dto.strokes());
        assertEquals(0, dto.strokes().size());
    }
}
