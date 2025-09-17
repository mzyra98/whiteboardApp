package pl.tablica.wbapp.dto.Autoryzacja;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Rejestracja {

    @NotBlank
    @Email
    @Size(max = 320)
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String haslo;

    @NotBlank
    @Size(max = 100)
    private String nazwaWyswietlana;

    @NotBlank
    @Size(max = 20)
    private String rola;

    public Rejestracja() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHaslo() { return haslo; }
    public void setHaslo(String haslo) { this.haslo = haslo; }
    public String getNazwaWyswietlana() { return nazwaWyswietlana; }
    public void setNazwaWyswietlana(String nazwaWyswietlana) { this.nazwaWyswietlana = nazwaWyswietlana; }
    public String getRola() { return rola; }
    public void setRola(String rola) { this.rola = rola; }
}
