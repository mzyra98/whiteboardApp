package pl.tablica.wbapp.konfiguracja;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

@ConfigurationProperties(prefix = "tablica.limity")
public class WlasciwosciLimitowTablic {

    public static class Zestaw {
        private Integer maxTablic;
        private Integer maxUczestnikowNaTablice;
        private Duration maxCzasTrwania;

        public Integer getMaxTablic() { return maxTablic; }
        public void setMaxTablic(Integer maxTablic) { this.maxTablic = maxTablic; }

        public Integer getMaxUczestnikowNaTablice() { return maxUczestnikowNaTablice; }
        public void setMaxUczestnikowNaTablice(Integer v) { this.maxUczestnikowNaTablice = v; }

        public Duration getMaxCzasTrwania() { return maxCzasTrwania; }
        public void setMaxCzasTrwania(Duration maxCzasTrwania) { this.maxCzasTrwania = maxCzasTrwania; }
    }

    private Zestaw uczen = new Zestaw();
    private Zestaw nauczyciel = new Zestaw();
    private Zestaw admin = new Zestaw();

    public Zestaw getUczen() { return uczen; }
    public void setUczen(Zestaw uczen) { this.uczen = uczen; }

    public Zestaw getNauczyciel() { return nauczyciel; }
    public void setNauczyciel(Zestaw nauczyciel) { this.nauczyciel = nauczyciel; }

    public Zestaw getAdmin() { return admin; }
    public void setAdmin(Zestaw admin) { this.admin = admin; }
}
