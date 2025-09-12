package pl.tablica.wbapp.dto;

public class NowaTablicaDto {
    private String tytul;
    private Long wlascicielId;

    public NowaTablicaDto() {}

    public NowaTablicaDto(String tytul, Long wlascicielId) {
        this.tytul = tytul;
        this.wlascicielId = wlascicielId;
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public Long getWlascicielId() {
        return wlascicielId;
    }

    public void setWlascicielId(Long wlascicielId) {
        this.wlascicielId = wlascicielId;
    }
}
