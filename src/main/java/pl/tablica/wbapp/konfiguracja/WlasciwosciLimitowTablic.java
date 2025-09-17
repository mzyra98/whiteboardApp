package pl.tablica.wbapp.konfiguracja;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tablica.limity")
public class WlasciwosciLimitowTablic {
    private Integer maksTablicNaUzytkownika;
    private Integer maksUczestnikowNaTablice;
    private long importMaxBytes = 1_048_576L;

    public Integer getMaksTablicNaUzytkownika() { return maksTablicNaUzytkownika; }
    public void setMaksTablicNaUzytkownika(Integer v) { this.maksTablicNaUzytkownika = v; }

    public Integer getMaksUczestnikowNaTablice() { return maksUczestnikowNaTablice; }
    public void setMaksUczestnikowNaTablice(Integer v) { this.maksUczestnikowNaTablice = v; }

    public long getImportMaxBytes() { return importMaxBytes; }
    public void setImportMaxBytes(long v) { this.importMaxBytes = v; }
}
