package pl.tablica.wbapp.usluga;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ObslugaBrakuDostepu implements AccessDeniedHandler {
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String msg = "Brak uprawnien";
        if ("DELETE".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().startsWith("/api/rysunki")) {
            msg = "Nauczyciel nie moze czyscic tablicy ucznia";
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 403);
        body.put("error", "Forbidden");
        body.put("message", msg);
        body.put("path", request.getRequestURI());

        om.writeValue(response.getOutputStream(), body);
    }
}
