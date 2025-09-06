package pl.tablica.wbapp.kontroler;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.dto.NowaTablicaDto;
import pl.tablica.wbapp.dto.TablicaDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.RolaUzytkownika;
import pl.tablica.wbapp.usluga.SerwisTablicy;

@RestController
@RequestMapping("/api/tablice")
public class KontrolerTablicy {

    private final SerwisTablicy serwis;

    public KontrolerTablicy(SerwisTablicy serwis) { this.serwis = serwis; }

    @PostMapping
    public ResponseEntity<TablicaDto> utworz(
            @AuthenticationPrincipal KontoUzytkownika zalogowany,
            @Valid @RequestBody NowaTablicaDto in) {

        if (zalogowany == null) {
            throw new IllegalArgumentException("Brak zalogowanego użytkownika.");
        }
        if (zalogowany.getRola() == RolaUzytkownika.UCZEN
                && !zalogowany.getId().equals(in.wlascicielId)) {
            throw new IllegalArgumentException("Uczeń nie może tworzyć tablicy dla innego użytkownika.");
        }
        return ResponseEntity.ok(serwis.utworz(in));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TablicaDto> pobierz(@PathVariable Long id) {
        return ResponseEntity.ok(serwis.pobierz(id));
    }

    @GetMapping
    public ResponseEntity<Page<TablicaDto>> lista(
            @AuthenticationPrincipal KontoUzytkownika zalogowany,
            @RequestParam(required = false) Long wlascicielId,
            @PageableDefault(sort = "createdAt") Pageable pageable) {

        if (zalogowany == null) {
            throw new IllegalArgumentException("Brak zalogowanego użytkownika.");
        }

        if (zalogowany.getRola() == RolaUzytkownika.UCZEN) {
            return ResponseEntity.ok(serwis.listaDlaWlasciciela(zalogowany.getId(), pageable));
        } else {
            Long id = (wlascicielId != null) ? wlascicielId : zalogowany.getId();
            return ResponseEntity.ok(serwis.listaDlaWlasciciela(id, pageable));
        }
    }
}



