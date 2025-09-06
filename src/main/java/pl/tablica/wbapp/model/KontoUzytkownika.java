package pl.tablica.wbapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "konto_uzytkownika", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class KontoUzytkownika extends EncjaBazowa {

    @NotBlank
    @Column(name = "nazwa_wyswietlana", nullable = false, length = 100)
    private String nazwaWyswietlana;

    @Email
    @NotBlank
    @Column(nullable = false, length = 320)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolaUzytkownika rola = RolaUzytkownika.UCZEN;

    @Column(name = "haslo_hash", nullable = false, length = 100)
    private String hasloHash;

    @Column(nullable = false)
    private boolean aktywny = true;

    public String getNazwaWyswietlana() { return nazwaWyswietlana; }
    public void setNazwaWyswietlana(String nazwaWyswietlana) { this.nazwaWyswietlana = nazwaWyswietlana; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public RolaUzytkownika getRola() { return rola; }
    public void setRola(RolaUzytkownika rola) { this.rola = rola; }
    public String getHasloHash() { return hasloHash; }
    public void setHasloHash(String hasloHash) { this.hasloHash = hasloHash; }
    public boolean isAktywny() { return aktywny; }
    public void setAktywny(boolean aktywny) { this.aktywny = aktywny; }

    public String getDisplayName() { return nazwaWyswietlana; }
    public void setDisplayName(String displayName) { this.nazwaWyswietlana = displayName; }
    public String getHaslo() { return hasloHash; }
    public void setHaslo(String haslo) { this.hasloHash = haslo; }
}