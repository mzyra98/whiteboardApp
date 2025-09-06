package pl.tablica.wbapp.statystyki.usluga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.RolaUzytkownika;
import pl.tablica.wbapp.model.Tablica;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumTablicy;
import pl.tablica.wbapp.statystyki.dto.StatystykiTablicyDto;
import pl.tablica.wbapp.wyjatek.ErrorCode;
import pl.tablica.wbapp.wyjatek.WyjatekAplikacji;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Service
public class SerwisStatystykTablicy {

    private static final Logger log = LoggerFactory.getLogger(SerwisStatystykTablicy.class);

    private final RepozytoriumTablicy repoTablicy;
    private final RepozytoriumKontaUzytkownika repoUzytk;
    private final JdbcTemplate jdbc;

    public SerwisStatystykTablicy(RepozytoriumTablicy repoTablicy,
                                  RepozytoriumKontaUzytkownika repoUzytk,
                                  JdbcTemplate jdbc) {
        this.repoTablicy = repoTablicy;
        this.repoUzytk = repoUzytk;
        this.jdbc = jdbc;
    }

    public StatystykiTablicyDto pobierzDla(Long idTablicy) {
        KontoUzytkownika u = biezacyUzytkownik();
        Tablica tablica = repoTablicy.findById(idTablicy)
                .orElseThrow(() -> new WyjatekAplikacji(ErrorCode.NIE_ZNALEZIONO_REKORDU, "Tablica nie istnieje: " + idTablicy));

        boolean admin = u.getRola() == RolaUzytkownika.ADMIN;
        Long ownerId = tablica.getWlasciciel() != null ? tablica.getWlasciciel().getId() : null;
        if (!admin && (ownerId == null || !Objects.equals(ownerId, u.getId()))) {
            throw new WyjatekAplikacji(ErrorCode.BRAK_UPRAWNIEN, "Brak dostępu do tablicy: " + idTablicy);
        }

        long linkiAktywne = 0L;
        Instant ostatniaZLinkow = null;
        if (istniejeTabelaBezpiecznie("linki_udostepnienia")) {
            String sqlAktywne = "select count(*) from linki_udostepnienia where tablica_id = ? and anulowany = false and (wygasa is null or wygasa > current_timestamp) and (maks_wejsc is null or (liczba_wejsc is not null and liczba_wejsc < maks_wejsc))";
            linkiAktywne = liczBezpiecznie(sqlAktywne, idTablicy);
            ostatniaZLinkow = maxCzasBezpiecznie("select max(utworzony) from linki_udostepnienia where tablica_id = ?", idTablicy);
        }

        long wspolpracownicy = 0L;
        if (istniejeTabelaBezpiecznie("wspolpracownicy_tablicy")) {
            wspolpracownicy = liczBezpiecznie("select count(*) from wspolpracownicy_tablicy where tablica_id = ?", idTablicy);
        }

        long pociagniecia = 0L;
        Instant ostatniaAktywnosc = ostatniaZLinkow;
        if (istniejeTabelaBezpiecznie("strokes")) {
            pociagniecia = liczBezpiecznie("select count(*) from strokes where tablica_id = ?", idTablicy);
            Instant maxRys = maxCzasBezpiecznie("select max(utworzony) from strokes where tablica_id = ?", idTablicy);
            ostatniaAktywnosc = max(ostatniaZLinkow, maxRys);
        }

        StatystykiTablicyDto dto = new StatystykiTablicyDto();
        dto.setIdTablicy(idTablicy);
        dto.setLiczbaPociagniec(pociagniecia);
        dto.setLiczbaWspolpracownikow(wspolpracownicy);
        dto.setLiczbaAktywnychLinkow(linkiAktywne);
        dto.setOstatniaAktywnosc(ostatniaAktywnosc);
        return dto;
    }

    private KontoUzytkownika biezacyUzytkownik() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated()) {
            throw new WyjatekAplikacji(ErrorCode.BRAK_UPRAWNIEN, "Brak uwierzytelnienia");
        }
        String email = a.getName();
        return repoUzytk.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new WyjatekAplikacji(ErrorCode.BRAK_UPRAWNIEN, "Użytkownik nie istnieje: " + email));
    }

    private long liczBezpiecznie(String sql, Long id) {
        try {
            Long v = jdbc.queryForObject(sql, Long.class, id);
            return v == null ? 0L : v;
        } catch (DataAccessException ex) {
            log.debug("SQL liczBezpiecznie: {}", ex.getMessage());
            return 0L;
        }
    }

    private Instant maxCzasBezpiecznie(String sql, Long id) {
        try {
            Timestamp ts = jdbc.queryForObject(sql, Timestamp.class, id);
            return ts == null ? null : ts.toInstant();
        } catch (DataAccessException ex) {
            log.debug("SQL maxCzasBezpiecznie: {}", ex.getMessage());
            return null;
        }
    }

    private Instant max(Instant a, Instant b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }

    private boolean istniejeTabelaBezpiecznie(String nazwa) {
        try {
            Integer c = jdbc.queryForObject(
                    "select count(*) from information_schema.tables where table_schema = database() and table_name = ?",
                    Integer.class, nazwa);
            if (c != null) return c > 0;
        } catch (DataAccessException ex) {
            log.debug("Brak information_schema: {}", ex.getMessage());
        }
        try {
            jdbc.queryForObject("select 1 from `" + nazwa + "` where 1=0", Integer.class);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
