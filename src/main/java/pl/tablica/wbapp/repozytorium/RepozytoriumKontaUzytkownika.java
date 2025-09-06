package pl.tablica.wbapp.repozytorium;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.tablica.wbapp.model.KontoUzytkownika;

public interface RepozytoriumKontaUzytkownika extends JpaRepository<KontoUzytkownika, Long> {

    Optional<KontoUzytkownika> findByEmailIgnoreCase(String email);
    Optional<KontoUzytkownika> findByNazwaWyswietlanaIgnoreCase(String nazwa);

    List<KontoUzytkownika> findTop20ByNazwaWyswietlanaContainingIgnoreCaseOrderByNazwaWyswietlanaAsc(String q);
}
