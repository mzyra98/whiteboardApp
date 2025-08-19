package pl.tablica.wbapp.statystyki.usluga;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.RolaUzytkownika;
import pl.tablica.wbapp.model.Tablica;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumTablicy;
import pl.tablica.wbapp.statystyki.dto.StatystykiTablicyDto;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class SerwisStatystykTablicy {

    @PersistenceContext
    private EntityManager em;

    private final RepozytoriumTablicy repoTablicy;
    private final RepozytoriumKontaUzytkownika repoUzytk;

    public SerwisStatystykTablicy(RepozytoriumTablicy repoTablicy, RepozytoriumKontaUzytkownika repoUzytk) {
        this.repoTablicy = repoTablicy;
        this.repoUzytk = repoUzytk;
    }

    public StatystykiTablicyDto pobierzDla(Long idTablicy, Long wywolujacyId) {
        Tablica t = repoTablicy.findById(idTablicy).orElseThrow();
        KontoUzytkownika u = repoUzytk.findById(wywolujacyId).orElseThrow();
        boolean admin = u.getRola() == RolaUzytkownika.ADMIN;
        if (!admin && !t.getWlasciciel().getId().equals(wywolujacyId)) throw new RuntimeException("Brak dostępu");

        long pociagniecia = zlicz("select count(*) from strokes where tablica_id=?1", idTablicy);
        long wsp = zlicz("select count(*) from wspolpracownicy_tablicy where tablica_id=?1", idTablicy);
        long linki = zlicz("select count(*) from linki_udostepnienia where tablica_id=?1 and anulowany=false and (wygasa is null or wygasa>current_timestamp)", idTablicy);

        Instant maxRys = maxCzas("select max(utworzony) from strokes where tablica_id=?1", idTablicy);
        Instant maxLink = maxCzas("select max(utworzony) from linki_udostepnienia where tablica_id=?1", idTablicy);
        Instant ostatnia = max(maxRys, maxLink);

        return new StatystykiTablicyDto(idTablicy, pociagniecia, wsp, linki, ostatnia);
    }

    private long zlicz(String sql, Long id) {
        Object o = em.createNativeQuery(sql).setParameter(1, id).getSingleResult();
        return ((Number) o).longValue();
    }

    private Instant maxCzas(String sql, Long id) {
        Object o = em.createNativeQuery(sql).setParameter(1, id).getSingleResult();
        if (o == null) return null;
        if (o instanceof Timestamp ts) return ts.toInstant();
        return null;
    }

    private Instant max(Instant a, Instant b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }
}