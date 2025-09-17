package pl.tablica.wbapp.kontroler;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.dto.Autoryzacja.Logowanie;
import pl.tablica.wbapp.dto.Autoryzacja.MojaOdpowiedz;
import pl.tablica.wbapp.dto.Autoryzacja.Odpowiedz;
import pl.tablica.wbapp.dto.Autoryzacja.OdswiezProces;
import pl.tablica.wbapp.dto.Autoryzacja.Rejestracja;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.usluga.SerwisAutoryzacji;

@RestController
@RequestMapping("/api/auth")
public class KontrolerAutoryzacji {

    private final SerwisAutoryzacji serwis;
    private final RepozytoriumKontaUzytkownika repozytorium;

    public KontrolerAutoryzacji(SerwisAutoryzacji serwis,
                                RepozytoriumKontaUzytkownika repozytorium) {
        this.serwis = serwis;
        this.repozytorium = repozytorium;
    }

    @PostMapping("/register")
    public ResponseEntity<Odpowiedz> register(@Valid @RequestBody Rejestracja dto) {
        Odpowiedz out = serwis.rejestruj(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PostMapping("/login")
    public ResponseEntity<Odpowiedz> login(@Valid @RequestBody Logowanie dto) {
        Odpowiedz out = serwis.login(dto);
        return ResponseEntity.ok(out);
    }

    @PostMapping("/odswiez")
    public ResponseEntity<Odpowiedz> odswiez(@Valid @RequestBody OdswiezProces dto) {
        Odpowiedz out = serwis.odswiez(dto);
        return ResponseEntity.ok(out);
    }

    @GetMapping("/me")
    public ResponseEntity<MojaOdpowiedz> me(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = auth.getName();
        KontoUzytkownika u = repozytorium.findByEmailIgnoreCase(email).orElse(null);
        if (u == null || !u.isAktywny()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(serwis.me(u));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
