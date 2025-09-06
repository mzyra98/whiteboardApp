package pl.tablica.wbapp.usluga;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;

import java.util.List;

@Service
public class DostawcaSzczegolowUzytkownika implements UserDetailsService {

    private final RepozytoriumKontaUzytkownika repozytorium;

    public DostawcaSzczegolowUzytkownika(RepozytoriumKontaUzytkownika repozytorium) {
        this.repozytorium = repozytorium;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        final String email = login == null ? "" : login.trim();
        KontoUzytkownika konto = repozytorium.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + email));

        String rola = "ROLE_" + (konto.getRola() == null ? "UCZEN" : konto.getRola().name());

        return new User(
                konto.getEmail(),
                konto.getHaslo(),
                List.of(new SimpleGrantedAuthority(rola))
        );
    }
}
