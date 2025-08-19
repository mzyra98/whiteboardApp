package pl.tablica.wbapp.udostepnianie.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "linki_udostepnienia", indexes = {
        @Index(name = "idx_lu_token", columnList = "token", unique = true),
        @Index(name = "idx_lu_tablica", columnList = "tablicaId")
})
public class LinkUdostepnienia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tablicaId;
    private Long tworcaUzytkownikId;
    @Enumerated(EnumType.STRING)
    private UprawnienieUdostepnienia uprawnienie;
    @Column(nullable = false, unique = true, length = 64)
    private String token;
    private Instant wygasa;
    private Integer maksWejsc;
    private Integer liczbaWejsc;
    private boolean anulowany;
    private Instant utworzony;

    public Long getId() { return id; }
    public Long getTablicaId() { return tablicaId; }
    public void setTablicaId(Long tablicaId) { this.tablicaId = tablicaId; }
    public Long getTworcaUzytkownikId() { return tworcaUzytkownikId; }
    public void setTworcaUzytkownikId(Long tworcaUzytkownikId) { this.tworcaUzytkownikId = tworcaUzytkownikId; }
    public UprawnienieUdostepnienia getUprawnienie() { return uprawnienie; }
    public void setUprawnienie(UprawnienieUdostepnienia uprawnienie) { this.uprawnienie = uprawnienie; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Instant getWygasa() { return wygasa; }
    public void setWygasa(Instant wygasa) { this.wygasa = wygasa; }
    public Integer getMaksWejsc() { return maksWejsc; }
    public void setMaksWejsc(Integer maksWejsc) { this.maksWejsc = maksWejsc; }
    public Integer getLiczbaWejsc() { return liczbaWejsc; }
    public void setLiczbaWejsc(Integer liczbaWejsc) { this.liczbaWejsc = liczbaWejsc; }
    public boolean isAnulowany() { return anulowany; }
    public void setAnulowany(boolean anulowany) { this.anulowany = anulowany; }
    public Instant getUtworzony() { return utworzony; }
    public void setUtworzony(Instant utworzony) { this.utworzony = utworzony; }
}
