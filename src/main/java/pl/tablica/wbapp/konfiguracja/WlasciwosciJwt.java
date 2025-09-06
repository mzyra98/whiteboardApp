package pl.tablica.wbapp.konfiguracja;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bezpieczenstwo.jwt")
public class WlasciwosciJwt {

    private long accessCzasSekundy;
    private long refreshCzasSekundy;
    private String issuer;
    private String tajny;

    public long getAccessCzasSekundy() { return accessCzasSekundy; }
    public void setAccessCzasSekundy(long accessCzasSekundy) { this.accessCzasSekundy = accessCzasSekundy; }

    public long getRefreshCzasSekundy() { return refreshCzasSekundy; }
    public void setRefreshCzasSekundy(long refreshCzasSekundy) { this.refreshCzasSekundy = refreshCzasSekundy; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getTajny() { return tajny; }
    public void setTajny(String tajny) { this.tajny = tajny; }

    public long getAccessTtl() { return accessCzasSekundy; }
    public long getRefreshTtl() { return refreshCzasSekundy; }
}