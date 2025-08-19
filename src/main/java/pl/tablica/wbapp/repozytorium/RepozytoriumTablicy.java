package pl.tablica.wbapp.repozytorium;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.tablica.wbapp.model.Tablica;

import java.util.List;

public interface RepozytoriumTablicy extends JpaRepository<Tablica, Long> {

    List<Tablica> findByWlasciciel_Id(Long wlascicielId);

    Page<Tablica> findByWlasciciel_Id(Long wlascicielId, Pageable pageable);

    void deleteByWlasciciel_Id(Long wlascicielId);
}