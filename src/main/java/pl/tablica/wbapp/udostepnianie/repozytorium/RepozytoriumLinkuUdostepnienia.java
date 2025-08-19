package pl.tablica.wbapp.udostepnianie.repozytorium;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import pl.tablica.wbapp.udostepnianie.model.LinkUdostepnienia;

public interface RepozytoriumLinkuUdostepnienia extends JpaRepository<LinkUdostepnienia, Long> {
    Optional<LinkUdostepnienia> findByToken(String token);
    List<LinkUdostepnienia> findByTablicaIdAndAnulowanyFalse(Long tablicaId);
}

