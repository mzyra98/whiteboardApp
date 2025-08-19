package pl.tablica.wbapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "strokes")
public class Pociagniecie extends EncjaBazowa {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Tablica tablica;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private KontoUzytkownika autor;

    @Lob
    @Column(nullable = false)
    private String dane;

    @Column(nullable = false)
    private String typ;  // np. "pen", "eraser"

    @Column(nullable = false)
    private LocalDateTime czas = LocalDateTime.now();

    public Tablica getTablica() { return tablica; }
    public void setTablica(Tablica tablica) { this.tablica = tablica; }
    public KontoUzytkownika getAutor() { return autor; }
    public void setAutor(KontoUzytkownika autor) { this.autor = autor; }
    public String getDane() { return dane; }
    public void setDane(String dane) { this.dane = dane; }
    public String getTyp() { return typ; }
    public void setTyp(String typ) { this.typ = typ; }
    public LocalDateTime getCzas() { return czas; }
    public void setCzas(LocalDateTime czas) { this.czas = czas; }
}

