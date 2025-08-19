package pl.tablica.wbapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class KontoUzytkownika extends EncjaBazowa {

    @NotBlank
    @Column(nullable = false, length = 60)
    private String displayName;

    @Email
    @NotBlank
    @Column(nullable = false, length = 120)
    private String email;
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Enumerated(EnumType.STRING)

    @Column(nullable = false, length = 20)
    private RolaUzytkownika rola = RolaUzytkownika.UCZEN;
    public RolaUzytkownika getRola() { return rola; }
    public void setRola(RolaUzytkownika rola) { this.rola = rola; }

    @Column(nullable = false, length = 100)
    private String haslo;
    public String getHaslo() { return haslo; }
    public void setHaslo(String haslo) { this.haslo = haslo; }
}

