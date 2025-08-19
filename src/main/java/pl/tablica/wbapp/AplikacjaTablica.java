package pl.tablica.wbapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AplikacjaTablica {
    public static void main(String[] args) {
        SpringApplication.run(AplikacjaTablica.class, args);
    }
}

