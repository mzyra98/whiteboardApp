package pl.tablica.wbapp.dto.Autoryzacja;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OdswiezProces {

    @NotBlank
    @Size(min = 16, max = 4096)
    private String refreshToken;

    public OdswiezProces() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}