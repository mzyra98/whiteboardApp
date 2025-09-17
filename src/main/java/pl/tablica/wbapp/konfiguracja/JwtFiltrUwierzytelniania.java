package pl.tablica.wbapp.konfiguracja;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFiltrUwierzytelniania extends OncePerRequestFilter {

    private final WlasciwosciJwt wlasciwosciJwt;

    public JwtFiltrUwierzytelniania(WlasciwosciJwt wlasciwosciJwt) {
        this.wlasciwosciJwt = wlasciwosciJwt;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {

        String aut = Optional.ofNullable(request.getHeader("Authorization")).orElse("");
        if (aut.startsWith("Bearer ")) {
            String token = aut.substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(wlasciwosciJwt.getSekret().getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String email = claims.getSubject();
                String rola = claims.get("rola", String.class);

                var auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        rola == null ? List.of() : List.of(new SimpleGrantedAuthority(rola))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }
}