package pl.tablica.wbapp.dto.Autoryzacja;

public class Odpowiedz {
    private String accessToken;
    private String refreshToken;

    public Odpowiedz(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
}