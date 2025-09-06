package pl.tablica.wbapp.dto;

import java.time.Instant;

public class WiadomoscCzatuOdpowiedzDto {
    private Long tablicaId;
    private Long autorId;
    private String tresc;
    private Instant czas;

    public Long getTablicaId() {
        return tablicaId;
    }

    public void setTablicaId(Long tablicaId) {
        this.tablicaId = tablicaId;
    }

    public Long getAutorId() {
        return autorId;
    }

    public void setAutorId(Long autorId) {
        this.autorId = autorId;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public Instant getCzas() {
        return czas;
    }

    public void setCzas(Instant czas) {
        this.czas = czas;
    }
}
