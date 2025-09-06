package pl.tablica.wbapp.konfiguracja;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors")
public class WlasciwosciCors {

    private List<String> dozwolone;

    public List<String> getDozwolone() {
        return dozwolone;
    }

    public void setDozwolone(List<String> dozwolone) {
        this.dozwolone = dozwolone;
    }
}