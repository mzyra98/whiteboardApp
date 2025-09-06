package pl.tablica.wbapp.dto;

import jakarta.validation.constraints.NotBlank;

public class LogowanieDto {
    @NotBlank public String login;
    @NotBlank public String haslo;
}
