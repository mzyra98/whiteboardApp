package pl.tablica.wbapp.usluga;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.dto.KontoUzytkownikaDto;
import pl.tablica.wbapp.dto.NoweKontoUzytkownikaDto;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.RolaUzytkownika;
import pl.tablica.wbapp.model.Tablica;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumPociagniec;
import pl.tablica.wbapp.repozytorium.RepozytoriumTablicy;
import pl.tablica.wbapp.wyjatek.ErrorCode;
import pl.tablica.wbapp.wyjatek.WyjatekAplikacji;
import java.util.List;

@Service
public class SerwisKontaUzytkownika {

    private final RepozytoriumKontaUzytkownika repozytorium;
    private final RepozytoriumTablicy repoTablicy;
    private final RepozytoriumPociagniec repoPociagniec;
    private final PasswordEncoder encoder;

    public SerwisKontaUzytkownika(RepozytoriumKontaUzytkownika repozytorium,
                                  RepozytoriumTablicy repoTablicy,
                                  RepozytoriumPociagniec repoPociagniec,
                                  PasswordEncoder encoder) {
        this.repozytorium = repozytorium;
        this.repoTablicy = repoTablicy;
        this.repoPociagniec = repoPociagniec;
        this.encoder = encoder;
    }

    @Transactional
    public Long utworz(NoweKontoUzytkownikaDto dane) {
        repozytorium.findByEmailIgnoreCase(dane.email).ifPresent(u -> {
            throw new WyjatekAplikacji(
                    ErrorCode.EMAIL_ZAJETY,
                    "Email '" + dane.email + "' jest już używany."
            );
        });

        if (dane.nazwaWyswietlana != null && !dane.nazwaWyswietlana.isBlank()) {
            repozytorium.findByNazwaWyswietlanaIgnoreCase(dane.nazwaWyswietlana).ifPresent(u -> {
                throw new WyjatekAplikacji(
                        ErrorCode.NAZWA_UZYTKOWNIKA_ZAJETA,
                        "Nazwa użytkownika '" + dane.nazwaWyswietlana + "' jest już zajęta."
                );
            });
        }

        var konto = new KontoUzytkownika();
        konto.setDisplayName(dane.nazwaWyswietlana);
        konto.setEmail(dane.email);
        konto.setHaslo(encoder.encode(dane.haslo));

        if (dane.rola != null && !dane.rola.isBlank()) {
            konto.setRola(RolaUzytkownika.valueOf(dane.rola));
        }

        repozytorium.save(konto);
        return konto.getId();
    }

    @Transactional(readOnly = true)
    public List<KontoUzytkownikaDto> lista() {
        return repozytorium.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<KontoUzytkownikaDto> listaStrona(Pageable pageable) {
        return repozytorium.findAll(pageable).map(this::toDto);
    }

    @Transactional
    public void zmienRole(Long idUzytkownika, RolaUzytkownika rola) {
        var u = repozytorium.findById(idUzytkownika)
                .orElseThrow(() -> new WyjatekAplikacji(
                        ErrorCode.NIE_ZNALEZIONO_REKORDU,
                        "Nie znaleziono użytkownika id=" + idUzytkownika
                ));
        u.setRola(rola);
        repozytorium.save(u);
    }

    @Transactional
    public void usun(Long idUzytkownika, boolean force) {
        if (!repozytorium.existsById(idUzytkownika)) {
            throw new WyjatekAplikacji(
                    ErrorCode.NIE_ZNALEZIONO_REKORDU,
                    "Nie znaleziono użytkownika id=" + idUzytkownika
            );
        }

        List<Tablica> tablice = repoTablicy.findByWlasciciel_Id(idUzytkownika);
        if (!tablice.isEmpty() && !force) {
            throw new WyjatekAplikacji(
                    ErrorCode.UZYTKOWNIK_MA_TABLICE,
                    "Użytkownik ma przypisane tablice – ustaw force=true, aby usunąć wraz z jego danymi."
            );
        }

        if (!tablice.isEmpty()) {
            var ids = tablice.stream().map(Tablica::getId).toList();
            repoPociagniec.deleteByTablica_IdIn(ids);
            repoTablicy.deleteAllById(ids);
        }

        repozytorium.deleteById(idUzytkownika);
    }

    private KontoUzytkownikaDto toDto(KontoUzytkownika k) {
        var dto = new KontoUzytkownikaDto();
        dto.id = k.getId();
        dto.nazwaWyswietlana = k.getDisplayName();
        dto.email = k.getEmail();
        return dto;
    }
}