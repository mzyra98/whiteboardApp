package pl.tablica.wbapp.udostepnianie.usluga;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.RolaUzytkownika;
import pl.tablica.wbapp.model.Tablica;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumTablicy;
import pl.tablica.wbapp.udostepnianie.dto.DolaczenieDoTablicyDto;
import pl.tablica.wbapp.udostepnianie.dto.LinkUdostepnieniaUtworzonyDto;
import pl.tablica.wbapp.udostepnianie.dto.UtworzLinkUdostepnieniaDto;
import pl.tablica.wbapp.udostepnianie.model.LinkUdostepnienia;
import pl.tablica.wbapp.udostepnianie.model.UprawnienieUdostepnienia;
import pl.tablica.wbapp.udostepnianie.model.WspolpracownikTablicy;
import pl.tablica.wbapp.udostepnianie.repozytorium.RepozytoriumLinkuUdostepnienia;
import pl.tablica.wbapp.udostepnianie.repozytorium.RepozytoriumWspolpracownikowTablicy;
import pl.tablica.wbapp.udostepnianie.wyjatek.NieZnaleziono;
import pl.tablica.wbapp.udostepnianie.wyjatek.Wygasl;
import pl.tablica.wbapp.udostepnianie.wyjatek.ZabronioneWywolanie;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;

@Service
public class SerwisUdostepniania {

    private final RepozytoriumLinkuUdostepnienia linki;
    private final RepozytoriumWspolpracownikowTablicy wspolpracownicy;
    private final RepozytoriumTablicy repoTablic;
    private final RepozytoriumKontaUzytkownika repoKont;
    private final SecureRandom los = new SecureRandom();

    public SerwisUdostepniania(RepozytoriumLinkuUdostepnienia linki,
                               RepozytoriumWspolpracownikowTablicy wspolpracownicy,
                               RepozytoriumTablicy repoTablic,
                               RepozytoriumKontaUzytkownika repoKont) {
        this.linki = linki;
        this.wspolpracownicy = wspolpracownicy;
        this.repoTablic = repoTablic;
        this.repoKont = repoKont;
    }

    private String token() {
        byte[] b = new byte[18];
        los.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private KontoUzytkownika uzytkownik(Long id) {
        return repoKont.findById(id).orElseThrow(NieZnaleziono::new);
    }

    private Tablica tablica(Long id) {
        return repoTablic.findById(id).orElseThrow(NieZnaleziono::new);
    }

    @Transactional
    public LinkUdostepnieniaUtworzonyDto utworz(Long uzytkownikId,
                                                Long tablicaId,
                                                UtworzLinkUdostepnieniaDto req,
                                                String bazowyUrl) {
        KontoUzytkownika u = uzytkownik(uzytkownikId);
        Tablica t = tablica(tablicaId);

        boolean admin = u.getRola() == RolaUzytkownika.ADMIN;
        boolean nauczyciel = u.getRola() == RolaUzytkownika.NAUCZYCIEL;
        boolean uczen = u.getRola() == RolaUzytkownika.UCZEN;
        boolean wlasciciel = t.getWlascicielId() != null && t.getWlascicielId().equals(uzytkownikId);

        if (!(admin || wlasciciel)) {
            throw new ZabronioneWywolanie();
        }

        int ttlMin = (req != null && req.getCzasWMinutach() != null)
                ? Math.max(1, req.getCzasWMinutach())
                : ((nauczyciel || admin) ? 48 * 60 : 240);

        int maks = (req != null && req.maksOsob != null)
                ? Math.max(1, req.maksOsob)
                : ((nauczyciel || admin) ? Integer.MAX_VALUE : 5);
        if (uczen && maks > 5) maks = 5;

        LinkUdostepnienia e = new LinkUdostepnienia();
        e.setTablicaId(tablicaId);
        e.setTworcaUzytkownikId(uzytkownikId);
        e.setUprawnienie(req != null && req.uprawnienie != null
                ? UprawnienieUdostepnienia.valueOf(req.uprawnienie)
                : UprawnienieUdostepnienia.EDYCJA);
        e.setToken(token());
        e.setWygasa(Instant.now().plusSeconds(ttlMin * 60L));
        e.setMaksWejsc(maks == Integer.MAX_VALUE ? null : maks);
        e.setLiczbaWejsc(0);
        e.setAnulowany(false);
        e.setUtworzony(Instant.now());
        linki.save(e);

        LinkUdostepnieniaUtworzonyDto out = new LinkUdostepnieniaUtworzonyDto();
        out.token = e.getToken();
        out.url = bazowyUrl + "/udostepnianie/dolacz/" + e.getToken();
        out.wygasa = (e.getWygasa() != null) ? OffsetDateTime.ofInstant(e.getWygasa(), ZoneOffset.UTC) : null;
        out.pozostaloWejsc = e.getMaksWejsc();
        return out;
    }

    @Transactional
    public DolaczenieDoTablicyDto dolacz(Long uzytkownikId, String token) {
        KontoUzytkownika u = uzytkownik(uzytkownikId);
        LinkUdostepnienia link = linki.findByToken(token).orElseThrow(NieZnaleziono::new);

        if (link.isAnulowany()) throw new Wygasl();
        if (link.getWygasa() != null && Instant.now().isAfter(link.getWygasa())) throw new Wygasl();
        if (link.getMaksWejsc() != null && link.getLiczbaWejsc() != null && link.getLiczbaWejsc() >= link.getMaksWejsc())
            throw new ZabronioneWywolanie();

        wspolpracownicy.findByTablicaIdAndUzytkownikId(link.getTablicaId(), u.getId()).orElseGet(() -> {
            WspolpracownikTablicy c = new WspolpracownikTablicy();
            c.setTablicaId(link.getTablicaId());
            c.setUzytkownikId(u.getId());
            c.setUprawnienie(link.getUprawnienie());
            c.setTymczasowy(true);
            return wspolpracownicy.save(c);
        });

        link.setLiczbaWejsc((link.getLiczbaWejsc() == null ? 0 : link.getLiczbaWejsc()) + 1);
        linki.save(link);

        DolaczenieDoTablicyDto out = new DolaczenieDoTablicyDto();
        out.tablicaId = link.getTablicaId();
        out.uprawnienie = link.getUprawnienie().name();
        return out;
    }

    @Transactional
    public void anuluj(Long uzytkownikId, String token) {
        KontoUzytkownika u = uzytkownik(uzytkownikId);
        LinkUdostepnienia link = linki.findByToken(token).orElseThrow(NieZnaleziono::new);
        boolean admin = u.getRola() == RolaUzytkownika.ADMIN;
        if (!(admin || link.getTworcaUzytkownikId().equals(u.getId()))) throw new ZabronioneWywolanie();
        link.setAnulowany(true);
        linki.save(link);
    }

    @Transactional(readOnly = true)
    public List<LinkUdostepnienia> lista(Long uzytkownikId, Long tablicaId) {
        Tablica t = tablica(tablicaId);
        boolean wlasciciel = t.getWlascicielId() != null && t.getWlascicielId().equals(uzytkownikId);
        KontoUzytkownika u = uzytkownik(uzytkownikId);
        boolean admin = u.getRola() == RolaUzytkownika.ADMIN;
        if (!(admin || wlasciciel)) throw new ZabronioneWywolanie();
        return linki.findByTablicaIdAndAnulowanyFalse(tablicaId);
    }
}