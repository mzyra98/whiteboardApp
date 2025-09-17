package pl.tablica.wbapp.dto.Autoryzacja;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OdswiezProces {
    @NotBlank
    @Size(min = 16, max = 4096)
    private String refreshToken;

    public OdswiezProces() {}

    @JsonCreator
    public OdswiezProces(@JsonProperty("refreshToken") String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}