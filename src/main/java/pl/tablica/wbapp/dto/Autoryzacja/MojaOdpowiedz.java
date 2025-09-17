package pl.tablica.wbapp.dto.Autoryzacja;

public class MojaOdpowiedz {
    private Long id;
    private String email;
    private String nazwaWyswietlana;
    private String rola;

    public MojaOdpowiedz() {}

    public MojaOdpowiedz(Long id, String email, String nazwaWyswietlana, String rola) {
        this.id = id;
        this.email = email;
        this.nazwaWyswietlana = nazwaWyswietlana;
        this.rola = rola;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNazwaWyswietlana() { return nazwaWyswietlana; }
    public void setNazwaWyswietlana(String nazwaWyswietlana) { this.nazwaWyswietlana = nazwaWyswietlana; }
    public String getRola() { return rola; }
    public void setRola(String rola) { this.rola = rola; }
}
