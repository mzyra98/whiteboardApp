package pl.tablica.wbapp.usluga.realizacja;

import pl.tablica.wbapp.dto.NowaTablicaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.dto.TablicaDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.Tablica;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumTablicy;

@Service
public class SerwisTablicy {

    private final RepozytoriumTablicy repoTablicy;
    private final RepozytoriumKontaUzytkownika repoKonta;

    public SerwisTablicy(RepozytoriumTablicy repoTablicy, RepozytoriumKontaUzytkownika repoKonta) {
        this.repoTablicy = repoTablicy;
        this.repoKonta = repoKonta;
    }

    @Transactional
    public TablicaDto utworz(NowaTablicaDto in) {
        KontoUzytkownika wlasciciel = repoKonta.findById(in.getWlascicielId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nie znaleziono właściciela id=" + in.getWlascicielId()));

        Tablica t = new Tablica();
        t.setTytul(in.getTytul());
        t.setWlasciciel(wlasciciel);

        t = repoTablicy.save(t);
        return toDto(t);
    }

    @Transactional(readOnly = true)
    public TablicaDto pobierz(Long id) {
        Tablica t = repoTablicy.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono tablicy id=" + id));
        return toDto(t);
    }

    @Transactional(readOnly = true)
    public Page<TablicaDto> listaDlaWlasciciela(Long wlascicielId, Pageable pageable) {
        return repoTablicy.findByWlasciciel_Id(wlascicielId, pageable).map(this::toDto);
    }

    private TablicaDto toDto(Tablica t) {
        TablicaDto dto = new TablicaDto();
        dto.id = t.getId();
        dto.tytul = t.getTytul();
        dto.wlascicielId = t.getWlasciciel().getId();
        dto.utworzona = t.getCreatedAt();
        dto.zmodyfikowana = t.getUpdatedAt();
        return dto;
    }
}

