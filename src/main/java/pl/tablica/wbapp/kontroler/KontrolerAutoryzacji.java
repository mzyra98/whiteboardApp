package pl.tablica.wbapp.kontroler;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.dto.Autoryzacja.*;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.usluga.SerwisAutoryzacji;

@RestController
@RequestMapping("/api/auth")
public class KontrolerAutoryzacji {

    private final SerwisAutoryzacji serwis;
    private final RepozytoriumKontaUzytkownika repo;

    public KontrolerAutoryzacji(SerwisAutoryzacji serwis, RepozytoriumKontaUzytkownika repo) {
        this.serwis = serwis;
        this.repo = repo;
    }

    @PostMapping("/rejestruj")
    public ResponseEntity<Odpowiedz> rejestruj(@Valid @RequestBody Rejestracja dto) {
        return ResponseEntity.ok(serwis.rejestruj(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<Odpowiedz> login(@Valid @RequestBody Logowanie dto) {
        return ResponseEntity.ok(serwis.login(dto));
    }

    @PostMapping("/odswiez")
    public ResponseEntity<Odpowiedz> odswiez(@Valid @RequestBody OdswiezProces dto) {
        return ResponseEntity.ok(serwis.odswiez(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<MojaOdpowiedz> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        String email = String.valueOf(auth.getPrincipal());
        KontoUzytkownika u = repo.findByEmailIgnoreCase(email).orElseThrow();
        return ResponseEntity.ok(serwis.me(u));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
