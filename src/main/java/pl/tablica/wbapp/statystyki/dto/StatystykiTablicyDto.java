package pl.tablica.wbapp.statystyki.dto;

import java.time.Instant;

public class StatystykiTablicyDto {
    private Long idTablicy;
    private Long liczbaPociagniec;
    private Long liczbaWspolpracownikow;
    private Long liczbaAktywnychLinkow;
    private Instant ostatniaAktywnosc;

    public StatystykiTablicyDto() {}

    public StatystykiTablicyDto(Long idTablicy, Long liczbaPociagniec, Long liczbaWspolpracownikow, Long liczbaAktywnychLinkow, Instant ostatniaAktywnosc) {
        this.idTablicy = idTablicy;
        this.liczbaPociagniec = liczbaPociagniec;
        this.liczbaWspolpracownikow = liczbaWspolpracownikow;
        this.liczbaAktywnychLinkow = liczbaAktywnychLinkow;
        this.ostatniaAktywnosc = ostatniaAktywnosc;
    }

    public Long getIdTablicy() { return idTablicy; }
    public void setIdTablicy(Long idTablicy) { this.idTablicy = idTablicy; }
    public Long getLiczbaPociagniec() { return liczbaPociagniec; }
    public void setLiczbaPociagniec(Long liczbaPociagniec) { this.liczbaPociagniec = liczbaPociagniec; }
    public Long getLiczbaWspolpracownikow() { return liczbaWspolpracownikow; }
    public void setLiczbaWspolpracownikow(Long liczbaWspolpracownikow) { this.liczbaWspolpracownikow = liczbaWspolpracownikow; }
    public Long getLiczbaAktywnychLinkow() { return liczbaAktywnychLinkow; }
    public void setLiczbaAktywnychLinkow(Long liczbaAktywnychLinkow) { this.liczbaAktywnychLinkow = liczbaAktywnychLinkow; }
    public Instant getOstatniaAktywnosc() { return ostatniaAktywnosc; }
    public void setOstatniaAktywnosc(Instant ostatniaAktywnosc) { this.ostatniaAktywnosc = ostatniaAktywnosc; }
}
