package pl.tablica.wbapp.konfiguracja;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;
import pl.tablica.wbapp.usluga.UslugaJwt;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class JwtFiltrUwierzytelniania extends OncePerRequestFilter {

    private final UslugaJwt uslugaJwt;
    private final RepozytoriumKontaUzytkownika repo;

    public JwtFiltrUwierzytelniania(UslugaJwt uslugaJwt, RepozytoriumKontaUzytkownika repo) {
        this.uslugaJwt = uslugaJwt;
        this.repo = repo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String h = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (h != null && h.toLowerCase(Locale.ROOT).startsWith("bearer ")) {
            String token = h.substring(7).trim();
            uslugaJwt.zweryfikuj(token).ifPresent(t -> {
                if (t.typ() == UslugaJwt.TypTokenu.ACCESS) {
                    repo.findByEmailIgnoreCase(t.email()).ifPresent(u -> {
                        String rola = "ROLE_" + u.getRola().name();
                        AbstractAuthenticationToken auth = new AbstractAuthenticationToken(
                                List.of(new SimpleGrantedAuthority(rola))) {
                            @Override public Object getCredentials() { return token; }
                            @Override public Object getPrincipal() { return u.getEmail(); }
                        };
                        auth.setAuthenticated(true);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
                }
            });
        }

        chain.doFilter(request, response);
    }
}