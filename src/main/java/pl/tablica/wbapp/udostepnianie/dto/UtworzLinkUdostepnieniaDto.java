package pl.tablica.wbapp.udostepnianie.dto;

public class UtworzLinkUdostepnieniaDto {
    public String uprawnienie;
    public Integer wygasa;
    public Integer czasWMinutach;
    public Integer maksOsob;

    public Integer getCzasWMinutach() {
        return (wygasa != null) ? wygasa : czasWMinutach;
    }
}