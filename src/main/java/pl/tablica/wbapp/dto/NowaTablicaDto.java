package pl.tablica.wbapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NowaTablicaDto {
    @NotBlank public String tytul;
    @NotNull  public Long wlascicielId;
}

