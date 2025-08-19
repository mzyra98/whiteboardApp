package pl.tablica.wbapp.kontroler;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.dto.KontoUzytkownikaDto;
import pl.tablica.wbapp.dto.ZmienRoleDto;
import pl.tablica.wbapp.usluga.SerwisKontaUzytkownika;

@RestController
@RequestMapping("/api/admin")
public class KontrolerAdmina {

    private final SerwisKontaUzytkownika serwis;

    public KontrolerAdmina(SerwisKontaUzytkownika serwis) {
        this.serwis = serwis;
    }

    @GetMapping("/uzytkownicy")
    public ResponseEntity<Page<KontoUzytkownikaDto>> listaStrona(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(serwis.listaStrona(pageable));
    }

    @PutMapping("/uzytkownicy/{id}/rola")
    public ResponseEntity<Void> zmienRole(
            @PathVariable Long id,
            @Valid @RequestBody ZmienRoleDto dto) {
        serwis.zmienRole(id, dto.rola);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/uzytkownicy/{id}")
    public ResponseEntity<Void> usun(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force) {
        serwis.usun(id, force);
        return ResponseEntity.noContent().build();
    }
}

