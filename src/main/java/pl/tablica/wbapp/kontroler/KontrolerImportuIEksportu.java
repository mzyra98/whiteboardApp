package pl.tablica.wbapp.kontroler;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.tablica.wbapp.dto.TablicaExportDto;
import pl.tablica.wbapp.usluga.UslugaRenderowaniaTablicy;
import pl.tablica.wbapp.usluga.realizacja.UslugaImportuIEksportu;

@RestController
@RequestMapping("/api/tablice")
public class KontrolerImportuIEksportu {
    private final UslugaImportuIEksportu usluga;
    private final UslugaRenderowaniaTablicy renderowanie;

    public KontrolerImportuIEksportu(UslugaImportuIEksportu usluga, UslugaRenderowaniaTablicy renderowanie) {
        this.usluga = usluga;
        this.renderowanie = renderowanie;
    }

    @GetMapping(value = "/{id}/export", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','NAUCZYCIEL','UCZEN')")
    public TablicaExportDto eksport(@PathVariable Long id, @RequestParam String email) {
        return usluga.eksportuj(id, email);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','NAUCZYCIEL','UCZEN')")
    public Long importuj(@RequestBody TablicaExportDto dto, @RequestParam String email) {
        return usluga.importuj(dto, email);
    }

    @GetMapping(value = "/{id}/export.png", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','NAUCZYCIEL','UCZEN')")
    public ResponseEntity<byte[]> eksportPng(@PathVariable Long id,
                                             @RequestParam(required = false) Integer width,
                                             @RequestParam(required = false) Integer height) {
        byte[] data = renderowanie.renderujPng(id, width, height);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("tablica-" + id + ".png").build());
        headers.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(headers).body(data);
    }

    @GetMapping(value = "/{id}/export.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','NAUCZYCIEL','UCZEN')")
    public ResponseEntity<byte[]> eksportPdf(@PathVariable Long id,
                                             @RequestParam(required = false) Integer width,
                                             @RequestParam(required = false) Integer height) {
        byte[] data = renderowanie.renderujPdf(id, width, height);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("tablica-" + id + ".pdf").build());
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(data);
    }
}
