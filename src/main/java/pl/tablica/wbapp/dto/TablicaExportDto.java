package pl.tablica.wbapp.dto;

import java.time.Instant;
import java.util.List;

public record TablicaExportDto(
        String schema,
        int version,
        Long id,
        String tytul,
        String wlascicielEmail,
        Instant utworzono,
        Instant zmodyfikowano,
        List<StrokeExportDto> strokes
) {}
