package pl.tablica.wbapp.dto;

import jakarta.validation.constraints.NotNull;
import pl.tablica.wbapp.model.RolaUzytkownika;

public class ZmienRoleDto {
    @NotNull
    public RolaUzytkownika rola;
}