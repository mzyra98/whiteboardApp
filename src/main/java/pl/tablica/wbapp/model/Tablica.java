package pl.tablica.wbapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tablice")
public class Tablica extends EncjaBazowa {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String tytul;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "wlasciciel_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tablica_wlasciciel"))
    private KontoUzytkownika wlasciciel;

    public String getTytul() { return tytul; }
    public void setTytul(String tytul) { this.tytul = tytul; }

    public KontoUzytkownika getWlasciciel() { return wlasciciel; }
    public void setWlasciciel(KontoUzytkownika wlasciciel) { this.wlasciciel = wlasciciel; }

    @Transient
    public Long getWlascicielId() {
        return wlasciciel != null ? wlasciciel.getId() : null;
    }
}
