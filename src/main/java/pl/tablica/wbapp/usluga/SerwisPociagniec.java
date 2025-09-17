package pl.tablica.wbapp.usluga;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.dto.PociagniecieDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.Pociagniecie;
import pl.tablica.wbapp.model.Tablica;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumPociagniec;
import pl.tablica.wbapp.repozytorium.RepozytoriumTablicy;
import pl.tablica.wbapp.wyjatek.ErrorCode;
import pl.tablica.wbapp.wyjatek.WyjatekAplikacji;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Service
public class SerwisPociagniec {

    private final RepozytoriumPociagniec repo;
    private final RepozytoriumTablicy repoTablicy;
    private final RepozytoriumKontaUzytkownika repoKonta;

    public SerwisPociagniec(RepozytoriumPociagniec repo,
                            RepozytoriumTablicy repoTablicy,
                            RepozytoriumKontaUzytkownika repoKonta) {
        this.repo = repo;
        this.repoTablicy = repoTablicy;
        this.repoKonta = repoKonta;
    }

    @Transactional
    public PociagniecieDto dodaj(@NonNull PociagniecieDto in) {
        Tablica t = repoTablicy.findById(in.tablicaId)
                .orElseThrow(() -> new WyjatekAplikacji(ErrorCode.NIE_ZNALEZIONO_REKORDU, "Tablica id=" + in.tablicaId));
        KontoUzytkownika a = repoKonta.findById(in.autorId)
                .orElseThrow(() -> new WyjatekAplikacji(ErrorCode.NIE_ZNALEZIONO_REKORDU, "Autor id=" + in.autorId));

        Pociagniecie p = new Pociagniecie();
        p.setTablica(t);
        p.setAutor(a);
        p.setTyp(in.typ);
        p.setDane(in.dane);
        p.setCzas(Instant.now());

        p = repo.save(p);
        return toDto(p);
    }

    @Transactional(readOnly = true)
    public List<PociagniecieDto> listaDlaTablicy(Long tablicaId) {
        return repo.findByTablica_IdOrderByCzasAsc(tablicaId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unused")
    public Page<PociagniecieDto> stronaDlaTablicy(Long tablicaId,
                                                  KontoUzytkownika zalogowany,
                                                  org.springframework.security.core.Authentication auth,
                                                  Pageable pageable) {
        List<PociagniecieDto> all = listaDlaTablicy(tablicaId);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<PociagniecieDto> content = (start >= all.size()) ? Collections.emptyList() : all.subList(start, end);
        return new PageImpl<>(content, pageable, all.size());
    }

    @Transactional
    public void wyczyscTablice(Long tablicaId,
                               KontoUzytkownika zalogowany,
                               org.springframework.security.core.Authentication auth) {

        var t = repoTablicy.findById(tablicaId)
                .orElseThrow(() -> new WyjatekAplikacji(ErrorCode.NIE_ZNALEZIONO_REKORDU, "Tablica id=" + tablicaId));

        boolean admin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        Long ownerId = (t.getWlasciciel() != null) ? t.getWlasciciel().getId() : null;
        Long userId = (zalogowany != null) ? zalogowany.getId() : null;
        boolean owner = ownerId != null && ownerId.equals(userId);

        if (!(admin || owner)) {
            throw new WyjatekAplikacji(ErrorCode.BRAK_UPRAWNIEN, "Brak uprawnień do czyszczenia tablicy " + tablicaId);
        }

        repo.deleteByTablica_Id(tablicaId);
    }

    private PociagniecieDto toDto(Pociagniecie p) {
        PociagniecieDto dto = new PociagniecieDto();
        dto.id = p.getId();
        dto.tablicaId = p.getTablica() != null ? p.getTablica().getId() : null;
        dto.autorId = p.getAutor() != null ? p.getAutor().getId() : null;
        dto.typ = p.getTyp();
        dto.dane = p.getDane();
        dto.czas = (p.getCzas() != null) ? LocalDateTime.ofInstant(p.getCzas(), ZoneOffset.UTC) : null;
        return dto;
    }
}
