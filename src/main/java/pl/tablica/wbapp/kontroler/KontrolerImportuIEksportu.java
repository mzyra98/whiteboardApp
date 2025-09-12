package pl.tablica.wbapp.kontroler;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.dto.TablicaDto;
import pl.tablica.wbapp.dto.TablicaExportDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.usluga.realizacja.SerwisTablicy;
import pl.tablica.wbapp.usluga.UslugaRenderowaniaTablicy;
import pl.tablica.wbapp.dto.NowaTablicaDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class KontrolerImportuIEksportu {

    private final SerwisTablicy serwis;
    private final UslugaRenderowaniaTablicy render;
    private final RepozytoriumKontaUzytkownika repoKonta;

    public KontrolerImportuIEksportu(SerwisTablicy serwis,
                                     UslugaRenderowaniaTablicy render,
                                     RepozytoriumKontaUzytkownika repoKonta) {
        this.serwis = serwis;
        this.render = render;
        this.repoKonta = repoKonta;
    }

    @PreAuthorize("hasAnyRole('NAUCZYCIEL','ADMIN')")
    @GetMapping({"/tablice/{id}/export", "/tablice/{id}/eksport"})
    public ResponseEntity<TablicaExportDto> exportJsonOne(@PathVariable Long id,
                                                          @AuthenticationPrincipal KontoUzytkownika me) {
        TablicaDto t = serwis.pobierz(id);
        String email = repoKonta.findById(t.wlascicielId).map(KontoUzytkownika::getEmail).orElse("");
        return ResponseEntity.ok(new TablicaExportDto(
                "whiteboard", 1, t.id, t.tytul, email, t.utworzona, t.zmodyfikowana, List.of()
        ));
    }

    @PreAuthorize("hasAnyRole('NAUCZYCIEL','ADMIN')")
    @GetMapping("/eksport")
    public ResponseEntity<List<TablicaExportDto>> exportAllJson(@AuthenticationPrincipal KontoUzytkownika me) {
        var page = serwis.listaDlaWlasciciela(me.getId(), Pageable.unpaged());
        List<TablicaExportDto> out = new ArrayList<>();
        page.forEach(t -> {
            String email = repoKonta.findById(t.wlascicielId).map(KontoUzytkownika::getEmail).orElse("");
            out.add(new TablicaExportDto(
                    "whiteboard", 1, t.id, t.tytul, email, t.utworzona, t.zmodyfikowana, List.of()
            ));
        });
        return ResponseEntity.ok(out);
    }

    @PreAuthorize("hasAnyRole('NAUCZYCIEL','ADMIN')")
    @GetMapping("/tablice/{id}/export.png")
    public ResponseEntity<byte[]> exportPng(@PathVariable Long id,
                                            @RequestParam(defaultValue = "1600") int width,
                                            @RequestParam(defaultValue = "900") int height,
                                            @AuthenticationPrincipal KontoUzytkownika me) {
        byte[] png = render.renderujPng(id, width, height);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=tablica-" + id + ".png")
                .body(png);
    }

    @PreAuthorize("hasAnyRole('NAUCZYCIEL','ADMIN')")
    @GetMapping("/tablice/{id}/export.pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id,
                                            @RequestParam(defaultValue = "1600") int width,
                                            @RequestParam(defaultValue = "900") int height,
                                            @AuthenticationPrincipal KontoUzytkownika me) {
        byte[] pdf = render.renderujPdf(id, width, height);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=tablica-" + id + ".pdf")
                .body(pdf);
    }

    @PostMapping("/api/import")
    public ResponseEntity<Void> importJson(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal KontoUzytkownika me) {

        Object tPl  = body.get("tytul");
        Object tAsci = body.get("tytul");
        Object nVal = body.get("nazwa");

        String tytul = tPl != null ? String.valueOf(tPl)
                : (tAsci != null ? String.valueOf(tAsci) : "Bez tytułu");

        NowaTablicaDto in = new NowaTablicaDto();
        in.setTytul(tytul);
        in.setWlascicielId(me.getId());

        serwis.utworz(in);
        return ResponseEntity.ok().build();
    }
}
