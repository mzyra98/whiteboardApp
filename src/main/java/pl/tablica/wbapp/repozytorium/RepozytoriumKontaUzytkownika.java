package pl.tablica.wbapp.repozytorium;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.tablica.wbapp.model.KontoUzytkownika;

import java.util.Optional;

public interface RepozytoriumKontaUzytkownika extends JpaRepository<KontoUzytkownika, Long> {
    Optional<KontoUzytkownika> findByEmail(String email);
}
