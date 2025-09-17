package pl.tablica.wbapp.dto.Autoryzacja;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Logowanie {

    @NotBlank
    @Email
    @Size(max = 320)
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String haslo;

    public Logowanie() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHaslo() { return haslo; }
    public void setHaslo(String haslo) { this.haslo = haslo; }
}
