package pl.tablica.wbapp.konfiguracja;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.RolaUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;

@Component
public class StarterDanychUzytkownikow implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StarterDanychUzytkownikow.class);

    private final RepozytoriumKontaUzytkownika repo;
    private final PasswordEncoder encoder;

    public StarterDanychUzytkownikow(RepozytoriumKontaUzytkownika repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        ustawLubDodajZHaslem("admin@local", "Administrator", RolaUzytkownika.ADMIN, "admin123");
        ustawLubDodajZHaslem("nauczyciel@local", "Pani/Pan Nauczyciel", RolaUzytkownika.NAUCZYCIEL, "nauczyciel123");
        ustawLubDodajZHaslem("uczen@local", "Uczeń Testowy", RolaUzytkownika.UCZEN, "uczen123");
    }

    private void ustawLubDodajZHaslem(String email, String nazwa, RolaUzytkownika rola, String suroweHaslo) {
        var istnieje = repo.findByEmailIgnoreCase(email);
        KontoUzytkownika u = istnieje.orElseGet(KontoUzytkownika::new);
        u.setEmail(email);
        u.setNazwaWyswietlana(nazwa);
        u.setRola(rola);
        u.setHasloHash(encoder.encode(suroweHaslo));
        u.setAktywny(true);
        repo.save(u);
        log.info("Konto DEV: {} ({}) – hasło ustawione.", email, rola);
    }
}