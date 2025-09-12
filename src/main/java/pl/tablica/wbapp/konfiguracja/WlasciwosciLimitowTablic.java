package pl.tablica.wbapp.konfiguracja;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tablica.limity")
public class WlasciwosciLimitowTablic {
    private Integer maksTablicNaUzytkownika;
    private Integer maksUczestnikowNaTablice;
    private long importMaxBytes = 1_048_576L;

    public Integer getMaksTablicNaUzytkownika() {
        return maksTablicNaUzytkownika;
    }

    public void setMaksTablicNaUzytkownika(Integer maksTablicNaUzytkownika) {
        this.maksTablicNaUzytkownika = maksTablicNaUzytkownika;
    }

    public Integer getMaksUczestnikowNaTablice() {
        return maksUczestnikowNaTablice;
    }

    public void setMaksUczestnikowNaTablice(Integer maksUczestnikowNaTablice) {
        this.maksUczestnikowNaTablice = maksUczestnikowNaTablice;
    }

    public long getImportMaxBytes() {
        return importMaxBytes;
    }

    public void setImportMaxBytes(long importMaxBytes) {
        this.importMaxBytes = importMaxBytes;
    }
}
