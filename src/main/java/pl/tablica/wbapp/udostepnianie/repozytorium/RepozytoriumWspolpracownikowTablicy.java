package pl.tablica.wbapp.udostepnianie.repozytorium;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import pl.tablica.wbapp.udostepnianie.model.WspolpracownikTablicy;

public interface RepozytoriumWspolpracownikowTablicy extends JpaRepository<WspolpracownikTablicy, Long> {
    Optional<WspolpracownikTablicy> findByTablicaIdAndUzytkownikId(Long tablicaId, Long uzytkownikId);
    long countByTablicaId(Long tablicaId);
}