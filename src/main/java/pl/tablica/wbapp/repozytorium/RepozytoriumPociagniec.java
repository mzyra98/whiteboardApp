package pl.tablica.wbapp.repozytorium;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.tablica.wbapp.model.Pociagniecie;

import java.util.List;

public interface RepozytoriumPociagniec extends JpaRepository<Pociagniecie, Long> {

    List<Pociagniecie> findByTablica_IdOrderByCzasAsc(Long tablicaId);

    void deleteByTablica_Id(Long tablicaId);

    void deleteByTablica_IdIn(List<Long> tablicaIds);
}