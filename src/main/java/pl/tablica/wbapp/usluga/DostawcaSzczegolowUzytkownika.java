package pl.tablica.wbapp.usluga;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;

import java.util.List;

@Service
public class DostawcaSzczegolowUzytkownika implements UserDetailsService {

    private final RepozytoriumKontaUzytkownika repo;

    public DostawcaSzczegolowUzytkownika(RepozytoriumKontaUzytkownika repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        KontoUzytkownika u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono uzytkownika: " + email));
        String rola = "ROLE_" + (u.getRola() == null ? "UCZEN" : u.getRola().name());
        return new User(u.getEmail(), u.getHaslo(), List.of(new SimpleGrantedAuthority(rola)));
    }
}

