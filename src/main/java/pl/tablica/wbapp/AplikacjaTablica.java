package pl.tablica.wbapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan(basePackages = "pl.tablica.wbapp")
public class AplikacjaTablica {
    public static void main(String[] args) {
        SpringApplication.run(AplikacjaTablica.class, args);
    }
}
