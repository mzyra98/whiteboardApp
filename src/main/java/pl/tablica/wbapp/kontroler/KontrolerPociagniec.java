package pl.tablica.wbapp.kontroler;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import pl.tablica.wbapp.dto.PociagniecieDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.usluga.SerwisPociagniec;

import java.util.List;

@RestController
@RequestMapping("/api/rysunki")
public class KontrolerPociagniec {

    private final SerwisPociagniec serwis;

    public KontrolerPociagniec(SerwisPociagniec serwis) {
        this.serwis = serwis;
    }

    @PostMapping
    public ResponseEntity<PociagniecieDto> dodaj(
            @AuthenticationPrincipal KontoUzytkownika zalogowany,
            @Valid @RequestBody PociagniecieDto in) {

        if (zalogowany == null || !zalogowany.getId().equals(in.autorId)) {
            throw new IllegalArgumentException("Autor musi byc zalogowanym uzytkownikiem.");
        }
        return ResponseEntity.ok(serwis.dodaj(in));
    }

    @GetMapping
    public ResponseEntity<List<PociagniecieDto>> lista(@RequestParam Long tablicaId) {
        return ResponseEntity.ok(serwis.listaDlaTablicy(tablicaId));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PociagniecieDto>> strona(
            @RequestParam Long tablicaId,
            @AuthenticationPrincipal KontoUzytkownika zalogowany,
            org.springframework.security.core.Authentication auth,
            @PageableDefault(size = 50, sort = "czas", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(serwis.stronaDlaTablicy(tablicaId, zalogowany, auth, pageable));
    }

    @DeleteMapping
    public ResponseEntity<Void> wyczysc(
            @RequestParam Long tablicaId,
            @AuthenticationPrincipal KontoUzytkownika zalogowany,
            org.springframework.security.core.Authentication auth) {
        serwis.wyczyscTablice(tablicaId, zalogowany, auth);
        return ResponseEntity.noContent().build();
    }
}
