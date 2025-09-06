package pl.tablica.wbapp.statystyki.kontroler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.tablica.wbapp.statystyki.dto.StatystykiTablicyDto;
import pl.tablica.wbapp.statystyki.usluga.SerwisStatystykTablicy;

@RestController
public class KontrolerStatystykTablicy {

    private final SerwisStatystykTablicy serwis;

    public KontrolerStatystykTablicy(SerwisStatystykTablicy serwis) {
        this.serwis = serwis;
    }

    @GetMapping("/api/statystyki/tablice/{id}")
    public ResponseEntity<StatystykiTablicyDto> statystykiNowa(@PathVariable Long id) {
        return ResponseEntity.ok(serwis.pobierzDla(id));
    }

    @GetMapping("/api/tablice/{id}/statystyki")
    public ResponseEntity<StatystykiTablicyDto> statystykiStara(@PathVariable Long id) {
        return ResponseEntity.ok(serwis.pobierzDla(id));
    }
}