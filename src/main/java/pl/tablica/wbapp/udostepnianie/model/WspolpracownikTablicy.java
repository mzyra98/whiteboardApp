package pl.tablica.wbapp.udostepnianie.model;

import jakarta.persistence.*;

@Entity
@Table(name = "wspolpracownicy_tablicy", uniqueConstraints = {
        @UniqueConstraint(name = "uq_tablica_uzytkownik", columnNames = {"tablicaId","uzytkownikId"})
})
public class WspolpracownikTablicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tablicaId;
    private Long uzytkownikId;
    @Enumerated(EnumType.STRING)
    private UprawnienieUdostepnienia uprawnienie;
    private boolean tymczasowy;

    public Long getId() { return id; }
    public Long getTablicaId() { return tablicaId; }
    public void setTablicaId(Long tablicaId) { this.tablicaId = tablicaId; }
    public Long getUzytkownikId() { return uzytkownikId; }
    public void setUzytkownikId(Long uzytkownikId) { this.uzytkownikId = uzytkownikId; }
    public UprawnienieUdostepnienia getUprawnienie() { return uprawnienie; }
    public void setUprawnienie(UprawnienieUdostepnienia uprawnienie) { this.uprawnienie = uprawnienie; }
    public boolean isTymczasowy() { return tymczasowy; }
    public void setTymczasowy(boolean tymczasowy) { this.tymczasowy = tymczasowy; }
}
