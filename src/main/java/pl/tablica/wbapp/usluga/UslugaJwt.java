package pl.tablica.wbapp.usluga;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.tablica.wbapp.konfiguracja.WlasciwosciJwt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class UslugaJwt {

    public enum TypTokenu { ACCESS, REFRESH }

    private final WlasciwosciJwt props;
    private final ObjectMapper mapper = new ObjectMapper();

    public UslugaJwt(WlasciwosciJwt props) {
        this.props = props;
    }

    public String generujAccessToken(long uzytkownikId, String email, String rola) {
        return generujToken(uzytkownikId, email, rola, TypTokenu.ACCESS);
    }

    public String generujRefreshToken(long uzytkownikId, String email, String rola) {
        return generujToken(uzytkownikId, email, rola, TypTokenu.REFRESH);
    }

    private String generujToken(long uzytkownikId, String email, String rola, TypTokenu typ) {
        try {
            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");

            Instant iat = Instant.now();
            long ttl = (typ == TypTokenu.ACCESS) ? props.getAccessTtl() : props.getRefreshTtl();
            Instant exp = iat.plusSeconds(ttl);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("iss", props.getIssuer());
            payload.put("sub", email);
            payload.put("iat", iat.getEpochSecond());
            payload.put("exp", exp.getEpochSecond());
            payload.put("uid", uzytkownikId);
            payload.put("rola", rola);
            payload.put("typTokenu", typ.name());

            String h = base64Url(mapper.writeValueAsBytes(header));
            String p = base64Url(mapper.writeValueAsBytes(payload));
            String s = podpis(h + "." + p);

            return h + "." + p + "." + s;
        } catch (Exception e) {
            throw new IllegalStateException("Nie udało się wygenerować JWT", e);
        }
    }

    public record ZweryfikowanyToken(String email, long uid, String rola, TypTokenu typ, Instant exp) {}

    public Optional<ZweryfikowanyToken> zweryfikuj(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return Optional.empty();

            String podpisWyliczony = podpis(parts[0] + "." + parts[1]);
            if (!stalePorownanie(podpisWyliczony, parts[2])) return Optional.empty();

            byte[] json = Base64.getUrlDecoder().decode(parts[1]);
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = mapper.readValue(json, Map.class);

            if (!Objects.equals(payload.get("iss"), props.getIssuer())) return Optional.empty();

            Number expSec = (Number) payload.get("exp");
            if (expSec == null) return Optional.empty();
            Instant exp = Instant.ofEpochSecond(expSec.longValue());
            if (Instant.now().isAfter(exp)) return Optional.empty();

            String sub = (String) payload.get("sub");
            Number uid = (Number) payload.get("uid");
            String rola = (String) payload.get("rola");
            String typ = (String) payload.get("typTokenu");
            if (sub == null || uid == null || rola == null || typ == null) return Optional.empty();

            return Optional.of(new ZweryfikowanyToken(sub, uid.longValue(), rola, TypTokenu.valueOf(typ), exp));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String podpis(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(props.getTajny().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64Url(sig);
    }

    private static String base64Url(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private static boolean stalePorownanie(String a, String b) {
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }
}