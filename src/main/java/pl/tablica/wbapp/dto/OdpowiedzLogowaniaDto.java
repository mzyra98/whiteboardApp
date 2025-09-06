package pl.tablica.wbapp.dto;

public class OdpowiedzLogowaniaDto {
    public Long id;
    public String nazwaWyswietlana;
    public String rola;

    public OdpowiedzLogowaniaDto(Long id, String nazwa, String rola) {
        this.id = id; this.nazwaWyswietlana = nazwa; this.rola = rola;
    }
}
