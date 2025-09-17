package pl.tablica.wbapp.repozytorium;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RepozytoriumPociagniecIT {

    @Autowired RepozytoriumPociagniec repoPociagniec;
    @Autowired RepozytoriumTablicy repoTablicy;
    @Autowired RepozytoriumKontaUzytkownika repoKonta;

    @Test
    void kontekst_i_iniekcja_dziala() {
        assertThat(repoPociagniec).isNotNull();
        assertThat(repoTablicy).isNotNull();
        assertThat(repoKonta).isNotNull();
    }
}