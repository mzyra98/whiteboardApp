package pl.tablica.wbapp.usluga;

public interface UslugaRenderowaniaTablicy {
    byte[] renderujPng(Long tablicaId, Integer szerokosc, Integer wysokosc);
    byte[] renderujPdf(Long tablicaId, Integer szerokosc, Integer wysokosc);
}