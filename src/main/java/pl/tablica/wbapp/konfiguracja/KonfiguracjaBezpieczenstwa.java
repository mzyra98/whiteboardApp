package pl.tablica.wbapp.konfiguracja;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class KonfiguracjaBezpieczenstwa {

    @Bean
    public SecurityFilterChain filtr(HttpSecurity http, JwtFiltrUwierzytelniania jwtFiltr) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());
        http.headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**", "/api/uwierzytelniania/**").permitAll()
                .requestMatchers("/h2-console/**", "/actuator/**", "/ws/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tablice").hasAnyRole("NAUCZYCIEL","ADMIN")
                .requestMatchers(HttpMethod.GET,
                        "/api/tablice/**/export",
                        "/api/tablice/**/eksport",
                        "/api/tablice/**/export.png",
                        "/api/tablice/**/export.pdf",
                        "/api/eksport").hasAnyRole("NAUCZYCIEL","ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/import").hasAnyRole("NAUCZYCIEL","ADMIN")
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtFiltr, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
