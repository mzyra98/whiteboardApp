package pl.tablica.wbapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({
        pl.tablica.wbapp.konfiguracja.WlasciwosciJwt.class,
        pl.tablica.wbapp.konfiguracja.WlasciwosciLimitowTablic.class
})
public class AplikacjaTablica {
    public static void main(String[] args) {
        SpringApplication.run(AplikacjaTablica.class, args);
    }
}