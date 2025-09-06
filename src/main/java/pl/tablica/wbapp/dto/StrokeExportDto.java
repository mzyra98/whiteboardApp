package pl.tablica.wbapp.dto;

import java.time.Instant;
import java.util.List;

public record StrokeExportDto(
        Long id,
        String autorEmail,
        String kolor,
        Integer grubosc,
        List<List<Integer>> punkty,
        Instant czas
) {}