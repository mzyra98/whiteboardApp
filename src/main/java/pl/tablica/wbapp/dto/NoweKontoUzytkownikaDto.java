package pl.tablica.wbapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank;

public class NoweKontoUzytkownikaDto {
    @NotBlank
    public String nazwaWyswietlana;

    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String haslo;
    public String rola;
}

