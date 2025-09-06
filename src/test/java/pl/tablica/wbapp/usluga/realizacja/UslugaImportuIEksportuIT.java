package pl.tablica.wbapp.usluga.realizacja;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.dto.StrokeExportDto;
import pl.tablica.wbapp.dto.TablicaExportDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class UslugaImportuIEksportuIT {

    @Autowired
    private UslugaImportuIEksportuDomyslneUstawienia usluga;

    @Autowired
    private RepozytoriumKontaUzytkownika kontoRepozytorium;

    private String wybierzEmailTestowy() {
        return kontoRepozytorium.findByEmailIgnoreCase("nauczyciel@local")
                .map(KontoUzytkownika::getEmail)
                .orElseGet(() -> kontoRepozytorium.findAll().stream()
                        .findFirst().map(KontoUzytkownika::getEmail)
                        .orElseThrow(() -> new IllegalStateException("Brak kont w bazie testowej")));
    }

    @Test
    void powinienWykonacOkraglyTripImportEksport() {
        var email = wybierzEmailTestowy();

        var dtoWej = new TablicaExportDto(
                "pl.tablica/board-export",
                1,
                null,
                "Tablica IT",
                email,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"),
                List.of(new StrokeExportDto(
                        null,
                        email,
                        "#000000",
                        3,
                        List.of(List.of(0, 0), List.of(10, 10)),
                        Instant.parse("2025-01-01T00:00:01Z")
                ))
        );

        Long noweId = usluga.importuj(dtoWej, email);
        assertNotNull(noweId);
        assertTrue(noweId > 0);

        var dtoWyj = usluga.eksportuj(noweId, email);
        assertNotNull(dtoWyj);
        assertEquals("pl.tablica/board-export", dtoWyj.schema());
        assertEquals(1, dtoWyj.version());
        assertEquals("Tablica IT", dtoWyj.tytul());
        assertEquals(email, dtoWyj.wlascicielEmail());
        assertNotNull(dtoWyj.utworzono());
        assertNotNull(dtoWyj.zmodyfikowano());
        assertNotNull(dtoWyj.strokes());
        assertEquals(1, dtoWyj.strokes().size());
        assertEquals(email, dtoWyj.strokes().get(0).autorEmail());
        assertNotNull(dtoWyj.strokes().get(0).czas());
    }
}
