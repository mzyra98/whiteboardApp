package pl.tablica.wbapp.udostepnianie.filtr;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.tablica.wbapp.udostepnianie.usluga.OgranicznikDolaczania;
import pl.tablica.wbapp.udostepnianie.wyjatek.ZbytWieleProb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(30)
public class FiltrOgraniczaniaDolaczania extends OncePerRequestFilter {

    private final OgranicznikDolaczania ogranicznik;

    public FiltrOgraniczaniaDolaczania(OgranicznikDolaczania ogranicznik) {
        this.ogranicznik = ogranicznik;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String p = request.getRequestURI();
        return p == null || !p.contains("/api/udostepnianie/dolacz");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();

        String uri = request.getRequestURI();
        String token = "";
        int idx = uri.lastIndexOf('/');
        if (idx > 0 && !uri.endsWith("/dolacz")) {
            token = uri.substring(idx + 1); // /dolacz/{token}
        }
        String klucz = ip + "|" + (token.isBlank() ? "BODY" : token);

        try {
            ogranicznik.wymagajPozwolenia(klucz);
            chain.doFilter(request, response);
        } catch (ZbytWieleProb ex) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            byte[] body = "{\"status\":429,\"code\":\"ZA_DUZO_ZADAN\",\"message\":\"Za dużo prób dołączenia. Spróbuj ponownie później.\"}"
                    .getBytes(StandardCharsets.UTF_8);
            response.getOutputStream().write(body);
        }
    }
}