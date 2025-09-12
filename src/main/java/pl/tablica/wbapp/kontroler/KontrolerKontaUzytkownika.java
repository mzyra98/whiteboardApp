package pl.tablica.wbapp.kontroler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.model.KontoUzytkownika;

import java.util.Map;

@RestController
@RequestMapping("/api/uzytkownicy")
public class KontrolerKontaUzytkownika {

    @GetMapping({"/me", "/whoami"})
    public ResponseEntity<Map<String, Object>> whoami(@AuthenticationPrincipal KontoUzytkownika u) {
        if (u == null) {
            return ResponseEntity.status(401).body(Map.of("blad", "Brak zalogowanego użytkownika."));
        }
        String email = u.getEmail() == null ? "" : u.getEmail();
        String display = u.getDisplayName() == null ? "" : u.getDisplayName();
        String rola = u.getRola() == null ? "" : u.getRola().name();

        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "email", email,
                "nazwaWyswietlana", display,
                "rola", rola
        ));
    }
}
