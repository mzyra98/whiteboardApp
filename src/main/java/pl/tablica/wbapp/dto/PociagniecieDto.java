package pl.tablica.wbapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class PociagniecieDto {
    public Long id;
    @NotNull public Long tablicaId;
    @NotNull public Long autorId;
    @NotBlank public String typ;
    @NotBlank public String dane;
    public LocalDateTime czas;
}
