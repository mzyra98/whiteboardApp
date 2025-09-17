package pl.tablica.wbapp.konfiguracja;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tablica.jwt")
public class WlasciwosciJwt {
    private String sekret;
    private long czasSekundy;

    public String getSekret() { return sekret; }
    public void setSekret(String sekret) { this.sekret = sekret; }

    public long getCzasSekundy() { return czasSekundy; }
    public void setCzasSekundy(long czasSekundy) { this.czasSekundy = czasSekundy; }
}
