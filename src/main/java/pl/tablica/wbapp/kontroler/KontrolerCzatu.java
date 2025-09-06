package pl.tablica.wbapp.kontroler;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pl.tablica.wbapp.dto.WiadomoscCzatuDto;
import pl.tablica.wbapp.dto.WiadomoscCzatuOdpowiedzDto;

import java.time.Instant;

@Controller
public class KontrolerCzatu {

    private final SimpMessagingTemplate messaging;

    public KontrolerCzatu(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @MessageMapping("/tablica.{id}")
    public void przyjmij(@DestinationVariable Long id, @Payload WiadomoscCzatuDto wiad) {
        WiadomoscCzatuOdpowiedzDto out = new WiadomoscCzatuOdpowiedzDto();
        out.setTablicaId(id);
        out.setAutorId(wiad.getAutorId());
        out.setTresc(wiad.getTresc());
        out.setCzas(Instant.now());
        messaging.convertAndSend("/topic/tablica." + id, out);
    }
}