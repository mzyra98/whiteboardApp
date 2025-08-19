package pl.tablica.wbapp.statystyki.kontroler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.statystyki.dto.StatystykiTablicyDto;
import pl.tablica.wbapp.statystyki.usluga.SerwisStatystykTablicy;

@RestController
@RequestMapping("/api/tablice")
public class KontrolerStatystykTablicy {

    private final SerwisStatystykTablicy serwis;

    public KontrolerStatystykTablicy(SerwisStatystykTablicy serwis) {
        this.serwis = serwis;
    }

    @GetMapping("/{id}/statystyki")
    public ResponseEntity<StatystykiTablicyDto> statystyki(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(serwis.pobierzDla(id, userId));
    }
}