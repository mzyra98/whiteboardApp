package pl.tablica.wbapp.kontroler;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.dto.KontoUzytkownikaDto;
import pl.tablica.wbapp.dto.NoweKontoUzytkownikaDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.usluga.SerwisKontaUzytkownika;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uzytkownicy")
public class KontrolerKontaUzytkownika {

    private final SerwisKontaUzytkownika serwis;

    public KontrolerKontaUzytkownika(SerwisKontaUzytkownika serwis) {
        this.serwis = serwis;
    }

    @PostMapping
    public ResponseEntity<Long> utworz(@Valid @RequestBody NoweKontoUzytkownikaDto dto) {
        return ResponseEntity.ok(serwis.utworz(dto));
    }

    @GetMapping
    public ResponseEntity<List<KontoUzytkownikaDto>> lista() {
        return ResponseEntity.ok(serwis.lista());
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @AuthenticationPrincipal KontoUzytkownika zalogowany) {

        if (zalogowany == null) {
            return ResponseEntity.status(401).build();
        }

        Map<String, Object> m = new HashMap<>();
        m.put("id", zalogowany.getId());
        m.put("email", zalogowany.getEmail());
        m.put("nazwaWyswietlana", zalogowany.getDisplayName());
        m.put("rola", zalogowany.getRola() == null ? null : zalogowany.getRola().name());
        return ResponseEntity.ok(m);
    }
    @GetMapping("/whoami")
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> whoami(
            @org.springframework.security.core.annotation.AuthenticationPrincipal pl.tablica.wbapp.model.KontoUzytkownika u) {
        var m = new java.util.HashMap<String, Object>();
        m.put("id", u.getId());
        m.put("email", u.getEmail());
        m.put("rola", u.getRola() == null ? null : u.getRola().name());
        return org.springframework.http.ResponseEntity.ok(m);
    }
}
