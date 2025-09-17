package pl.tablica.wbapp.usluga;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pl.tablica.wbapp.konfiguracja.WlasciwosciJwt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UslugaJwt {

    private final WlasciwosciJwt wlasciwosciJwt;
    private final ObjectMapper mapper;

    public UslugaJwt(WlasciwosciJwt wlasciwosciJwt, ObjectMapper mapper) {
        this.wlasciwosciJwt = wlasciwosciJwt;
        this.mapper = mapper;
    }

    public String generujAccessToken(@NonNull Long uid,
                                     @NonNull String email,
                                     @NonNull String rola) {
        long ttl = pobierzTtlSekundy();
        return makeToken(uid, email, rola, ttl);
    }

    public String generujRefreshToken(@NonNull Long uid,
                                      @NonNull String email,
                                      @NonNull String rola) {
        long ttl = pobierzTtlSekundy();
        long refreshTtl = Math.max(ttl * 2, ttl);
        return makeToken(uid, email, rola, refreshTtl);
    }

    public Optional<Map<String, Object>> zweryfikuj(@NonNull String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return Optional.empty();

            String headerPayload = parts[0] + "." + parts[1];
            String expectedSig = podpis(headerPayload);
            if (!stalePorownanie(expectedSig, parts[2])) return Optional.empty();

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> claims = mapper.readValue(payloadJson, new TypeReference<>() {
            });
            Object exp = claims.get("exp");
            if (exp instanceof Number && Instant.now().getEpochSecond() > ((Number) exp).longValue()) {
                return Optional.empty();
            }
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String makeToken(@NonNull Long uid,
                             @NonNull String email,
                             @NonNull String rola,
                             long ttlSeconds) {
        try {
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            long now = Instant.now().getEpochSecond();
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("uid", uid);
            payload.put("sub", email);
            payload.put("rola", rola);
            payload.put("iat", now);
            payload.put("exp", now + ttlSeconds);

            String h = base64Url(mapper.writeValueAsBytes(header));
            String p = base64Url(mapper.writeValueAsBytes(payload));
            String hp = h + "." + p;
            String s = podpis(hp);
            return hp + "." + s;
        } catch (Exception e) {
            throw new IllegalStateException("Nie udało się zbudować tokenu JWT", e);
        }
    }

    private String podpis(@NonNull String data) throws Exception {
        String secret = Optional.ofNullable(wlasciwosciJwt.getSekret())
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalStateException("Brak wartości tablica.jwt.sekret"));
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64Url(sig);
    }

    private static String base64Url(@NonNull byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private static boolean stalePorownanie(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }

    private long pobierzTtlSekundy() {
        String[] kandydaci = {"getCzasSekundy", "getCzasTrwania", "getCzas", "getTtlSekundy"};
        for (String nazwa : kandydaci) {
            try {
                Method m = WlasciwosciJwt.class.getMethod(nazwa);
                Object v = m.invoke(wlasciwosciJwt);
                if (v instanceof Number) return ((Number) v).longValue();
            } catch (ReflectiveOperationException ignored) {}
        }
        return 3600L;
    }
}
