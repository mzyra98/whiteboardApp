package pl.tablica.wbapp.usluga;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.tablica.wbapp.dto.PociagniecieDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.Pociagniecie;
import pl.tablica.wbapp.model.Tablica;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumPociagniec;
import pl.tablica.wbapp.repozytorium.RepozytoriumTablicy;

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
    public PociagniecieDto dodaj(PociagniecieDto in) {
        Tablica t = repoTablicy.findById(in.tablicaId)
                .orElseThrow(() -> new IllegalArgumentException("Brak tablicy id=" + in.tablicaId));
        KontoUzytkownika a = repoKonta.findById(in.autorId)
                .orElseThrow(() -> new IllegalArgumentException("Brak autora id=" + in.autorId));

        Pociagniecie p = new Pociagniecie();
        p.setTablica(t);
        p.setAutor(a);
        p.setTyp(in.typ);
        p.setDane(in.dane);

        p = repo.save(p);
        return toDto(p);
    }

    @Transactional(readOnly = true)
    public List<PociagniecieDto> listaDlaTablicy(Long tablicaId) {
        return repo.findByTablica_IdOrderByCzasAsc(tablicaId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<PociagniecieDto> stronaDlaTablicy(
            Long tablicaId,
            KontoUzytkownika zalogowany,
            org.springframework.security.core.Authentication auth,
            Pageable pageable) {

        List<PociagniecieDto> all = listaDlaTablicy(tablicaId);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<PociagniecieDto> content = (start >= all.size()) ? Collections.emptyList() : all.subList(start, end);
        return new PageImpl<>(content, pageable, all.size());
    }

    private PociagniecieDto toDto(Pociagniecie p) {
        PociagniecieDto dto = new PociagniecieDto();
        dto.id = p.getId();
        dto.tablicaId = p.getTablica().getId();
        dto.autorId = p.getAutor().getId();
        dto.typ = p.getTyp();
        dto.dane = p.getDane();
        dto.czas = p.getCzas();
        return dto;
    }

    @org.springframework.transaction.annotation.Transactional
    public void wyczyscTablice(
            Long tablicaId,
            pl.tablica.wbapp.model.KontoUzytkownika zalogowany,
            org.springframework.security.core.Authentication auth) {

        var t = repoTablicy.findById(tablicaId)
                .orElseThrow(() -> new IllegalArgumentException("Brak tablicy id=" + tablicaId));

        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean owner = t.getWlasciciel().getId().equals(zalogowany.getId());

        if (!(admin || owner)) {
            throw new IllegalArgumentException("Brak uprawnien do czyszczenia tej tablicy");
        }

        repo.deleteByTablica_Id(tablicaId);
    }
}
