package pl.tablica.wbapp.usluga;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.dto.Autoryzacja.Logowanie;
import pl.tablica.wbapp.dto.Autoryzacja.MojaOdpowiedz;
import pl.tablica.wbapp.dto.Autoryzacja.Odpowiedz;
import pl.tablica.wbapp.dto.Autoryzacja.OdswiezProces;
import pl.tablica.wbapp.dto.Autoryzacja.Rejestracja;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.RolaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.wyjatek.ErrorCode;
import pl.tablica.wbapp.wyjatek.KolizjaWartosci;
import pl.tablica.wbapp.wyjatek.WyjatekAplikacji;

@Service
public class SerwisAutoryzacji {

    private final RepozytoriumKontaUzytkownika repo;
    private final PasswordEncoder encoder;
    private final UslugaJwt jwt;

    public SerwisAutoryzacji(RepozytoriumKontaUzytkownika repo,
                             PasswordEncoder encoder,
                             UslugaJwt jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public Odpowiedz rejestruj(Rejestracja dto) {
        repo.findByEmailIgnoreCase(dto.getEmail()).ifPresent(u -> {
            throw new KolizjaWartosci(ErrorCode.EMAIL_ZAJETY, "email");
        });

        RolaUzytkownika rola = RolaUzytkownika.valueOf(dto.getRola().toUpperCase());
        KontoUzytkownika u = new KontoUzytkownika();
        u.setEmail(dto.getEmail().trim());
        u.setNazwaWyswietlana(dto.getNazwaWyswietlana().trim());
        u.setRola(rola);
        u.setHasloHash(encoder.encode(dto.getHaslo()));
        u.setAktywny(true);

        repo.save(u);

        String access = jwt.generujAccessToken(u.getId(), u.getEmail(), u.getRola().name());
        String refresh = jwt.generujRefreshToken(u.getId(), u.getEmail(), u.getRola().name());
        return new Odpowiedz(access, refresh);
    }

    public Odpowiedz login(Logowanie dto) {
        KontoUzytkownika u = repo.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new WyjatekAplikacji(ErrorCode.NIEPRAWIDLOWE_CREDENCJALE, "Nieprawidłowy e-mail lub hasło"));
        if (!u.isAktywny() || !encoder.matches(dto.getHaslo(), u.getHasloHash())) {
            throw new WyjatekAplikacji(ErrorCode.NIEPRAWIDLOWE_CREDENCJALE, "Nieprawidłowy e-mail lub hasło");
        }
        String access = jwt.generujAccessToken(u.getId(), u.getEmail(), u.getRola().name());
        String refresh = jwt.generujRefreshToken(u.getId(), u.getEmail(), u.getRola().name());
        return new Odpowiedz(access, refresh);
    }

    public Odpowiedz odswiez(OdswiezProces dto) {
        var zw = jwt.zweryfikuj(dto.getRefreshToken())
                .orElseThrow(() -> new WyjatekAplikacji(ErrorCode.NIEPRAWIDLOWE_CREDENCJALE, "Refresh token jest nieprawidłowy lub wygasł"));
        if (zw.typ() != UslugaJwt.TypTokenu.REFRESH) {
            throw new WyjatekAplikacji(ErrorCode.NIEPRAWIDLOWE_CREDENCJALE, "Przekazano nieprawidłowy typ tokenu");
        }
        KontoUzytkownika u = repo.findByEmailIgnoreCase(zw.email())
                .orElseThrow(() -> new WyjatekAplikacji(ErrorCode.NIEPRAWIDLOWE_CREDENCJALE, "Użytkownik nie istnieje"));
        String access = jwt.generujAccessToken(u.getId(), u.getEmail(), u.getRola().name());
        String refresh = jwt.generujRefreshToken(u.getId(), u.getEmail(), u.getRola().name());
        return new Odpowiedz(access, refresh);
    }

    public MojaOdpowiedz me(KontoUzytkownika u) {
        return new MojaOdpowiedz(u.getId(), u.getEmail(), u.getNazwaWyswietlana(), u.getRola().name());
    }
}