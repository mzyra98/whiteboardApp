package pl.tablica.wbapp.kontroler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.dto.NowaTablicaDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.usluga.realizacja.SerwisTablicy;

import java.util.Map;

@RestController
@RequestMapping("/api/tablice")
public class KontrolerTablicy {

    private final SerwisTablicy serwis;

    public KontrolerTablicy(SerwisTablicy serwis) {
        this.serwis = serwis;
    }

    @PostMapping
    public ResponseEntity<?> utworz(@AuthenticationPrincipal KontoUzytkownika u,
                                    @RequestBody NowaTablicaDto in) {
        if (u == null) {
            return ResponseEntity.status(401).body(Map.of("blad", "Brak zalogowanego użytkownika."));
        }
        in.setWlascicielId(u.getId());
        return ResponseEntity.ok(serwis.utworz(in));
    }
}
