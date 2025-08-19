package pl.tablica.wbapp.usluga;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.repozytorium.RepozytoriumKontaUzytkownika;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class FiltrNaglowekUserId extends OncePerRequestFilter {

    private final RepozytoriumKontaUzytkownika repo;

    public FiltrNaglowekUserId(RepozytoriumKontaUzytkownika repo) {
        this.repo = repo;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("X-User-Id");
        System.out.println("[Filtr] " + request.getMethod() + " " + request.getRequestURI() + " X-User-Id=" + header);

        if (header != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Long id = Long.valueOf(header.trim());
                Optional<KontoUzytkownika> opt = repo.findById(id);
                if (opt.isPresent()) {
                    KontoUzytkownika u = opt.get();
                    String rola = "ROLE_" + (u.getRola() == null ? "UCZEN" : u.getRola().name());
                    var auth = new UsernamePasswordAuthenticationToken(
                            u, null, List.of(new SimpleGrantedAuthority(rola)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("[Filtr] set auth id=" + u.getId() + " rola=" + u.getRola());
                } else {
                    System.out.println("[Filtr] brak użytkownika o id=" + id);
                }
            } catch (NumberFormatException ex) {
                System.out.println("[Filtr] X-User-Id nie jest liczbą: " + header);
            }
        }

        filterChain.doFilter(request, response);
    }
}

