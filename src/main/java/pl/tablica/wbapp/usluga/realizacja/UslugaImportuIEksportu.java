package pl.tablica.wbapp.usluga.realizacja;

import pl.tablica.wbapp.dto.TablicaExportDto;

public interface UslugaImportuIEksportu {
    TablicaExportDto eksportuj(Long tablicaId, String emailZglaszajacego);
    Long importuj(TablicaExportDto dto, String emailZglaszajacego);
}
