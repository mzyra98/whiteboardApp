package pl.tablica.wbapp.dto.Autoryzacja;

public class MojaOdpowiedz {
    private Long id;
    private String email;
    private String nazwaWyswietlana;
    private String rola;

    public MojaOdpowiedz(Long id, String email, String nazwaWyswietlana, String rola) {
        this.id = id;
        this.email = email;
        this.nazwaWyswietlana = nazwaWyswietlana;
        this.rola = rola;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getNazwaWyswietlana() { return nazwaWyswietlana; }
    public String getRola() { return rola; }
}
