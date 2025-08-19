package pl.tablica.wbapp.udostepnianie.kontroler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import pl.tablica.wbapp.udostepnianie.dto.*;
import pl.tablica.wbapp.udostepnianie.model.LinkUdostepnienia;
import pl.tablica.wbapp.udostepnianie.usluga.SerwisUdostepniania;

@RestController
@RequestMapping("/api")
public class KontrolerUdostepniania {
    private final SerwisUdostepniania serwis;

    public KontrolerUdostepniania(SerwisUdostepniania serwis) {
        this.serwis = serwis;
    }

    @PostMapping("/tablice/{tablicaId}/udostepnij")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkUdostepnieniaUtworzonyDto utworz(
            @RequestHeader("X-User-Id") Long uzytkownikId,
            @PathVariable Long tablicaId,
            @RequestBody(required = false) UtworzLinkUdostepnieniaDto req,
            @RequestHeader("Host") String host
    ) {
        String bazowy = "https://" + host + "/api";
        return serwis.utworz(uzytkownikId, tablicaId, req, bazowy);
    }

    @PostMapping("/udostepnianie/dolacz")
    public DolaczenieDoTablicyDto dolaczBody(
            @RequestHeader("X-User-Id") Long uzytkownikId,
            @RequestBody ZgloszenieDolaczeniaDto req
    ) {
        return serwis.dolacz(uzytkownikId, req.getToken());
    }

    @PostMapping("/udostepnianie/dolacz/{token}")
    public DolaczenieDoTablicyDto dolaczPath(
            @RequestHeader("X-User-Id") Long uzytkownikId,
            @PathVariable String token
    ) {
        return serwis.dolacz(uzytkownikId, token);
    }

    @DeleteMapping("/udostepnianie/anuluj/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anuluj(
            @RequestHeader("X-User-Id") Long uzytkownikId,
            @PathVariable String token
    ) {
        serwis.anuluj(uzytkownikId, token);
    }

    @GetMapping("/tablice/{tablicaId}/udostepnienia")
    public List<LinkUdostepnienia> lista(
            @RequestHeader("X-User-Id") Long uzytkownikId,
            @PathVariable Long tablicaId
    ) {
        return serwis.lista(uzytkownikId, tablicaId);
    }
}

