package pl.tablica.wbapp.udostepnianie.dto;

import jakarta.validation.constraints.NotBlank;

public class ZgloszenieDolaczeniaDto {
    @NotBlank
    public String token;

    public String getToken() {
        return token;
    }
}