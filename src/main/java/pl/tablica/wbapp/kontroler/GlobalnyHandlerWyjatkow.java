package pl.tablica.wbapp.kontroler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import pl.tablica.wbapp.wyjatek.ErrorCode;
import pl.tablica.wbapp.wyjatek.KolizjaWartosci;
import pl.tablica.wbapp.wyjatek.WyjatekAplikacji;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalnyHandlerWyjatkow {

    @ExceptionHandler(WyjatekAplikacji.class)
    public ResponseEntity<Map<String, Object>> wyjatekAplikacji(WyjatekAplikacji ex, HttpServletRequest req) {
        ErrorCode kod = ex.getCode();
        return ResponseEntity.status(kod.getHttpStatus()).body(body(kod, ex.getMessage(), req));
    }

    @ExceptionHandler(KolizjaWartosci.class)
    public ResponseEntity<Map<String, Object>> kolizja(KolizjaWartosci ex, HttpServletRequest req) {
        ErrorCode kod = ex.getCode();
        return ResponseEntity.status(kod.getHttpStatus()).body(body(kod, ex.getMessage(), req));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> bledyWalidacji(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ErrorCode kod = ErrorCode.BLEDY_WALIDACJI;
        List<String> detale = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
        Map<String, Object> payload = body(kod, kod.getDomyslnyKomunikat(), req);
        payload.put("detale", detale);
        return ResponseEntity.status(kod.getHttpStatus()).body(payload);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> naruszenie(ConstraintViolationException ex, HttpServletRequest req) {
        ErrorCode kod = ErrorCode.BLEDY_WALIDACJI;
        Map<String, Object> payload = body(kod, kod.getDomyslnyKomunikat(), req);
        payload.put("detale", ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList());
        return ResponseEntity.status(kod.getHttpStatus()).body(payload);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> brakParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        ErrorCode kod = ErrorCode.NIEPOPRAWNE_DANE_WEJSCIOWE;
        return ResponseEntity.status(kod.getHttpStatus())
                .body(body(kod, "Brak parametru: " + ex.getParameterName(), req));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> zlyTyp(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        ErrorCode kod = ErrorCode.NIEPOPRAWNE_DANE_WEJSCIOWE;
        return ResponseEntity.status(kod.getHttpStatus()).body(body(kod, ex.getMessage(), req));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> brakUprawnien(AccessDeniedException ex, HttpServletRequest req) {
        ErrorCode kod = ErrorCode.BRAK_UPRAWNIEN;
        return ResponseEntity.status(kod.getHttpStatus()).body(body(kod, kod.getDomyslnyKomunikat(), req));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> zlaMetoda(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        ErrorCode kod = ErrorCode.NIEDOZWOLONA_METODA;
        return ResponseEntity.status(kod.getHttpStatus()).body(body(kod, kod.getDomyslnyKomunikat(), req));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> nieZnaleziono(NoHandlerFoundException ex, HttpServletRequest req) {
        ErrorCode kod = ErrorCode.NIE_ZNALEZIONO_REKORDU;
        return ResponseEntity.status(kod.getHttpStatus()).body(body(kod, kod.getDomyslnyKomunikat(), req));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> naruszenieBD(DataIntegrityViolationException ex, HttpServletRequest req) {
        ErrorCode kod = ErrorCode.NIEPOPRAWNE_DANE_WEJSCIOWE;
        return ResponseEntity.status(kod.getHttpStatus()).body(body(kod, kod.getDomyslnyKomunikat(), req));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> fallback(Exception ex, HttpServletRequest req) {
        ErrorCode kod = ErrorCode.WEWNETRZNY_BLAD;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body(kod, kod.getDomyslnyKomunikat(), req));
    }

    private Map<String, Object> body(ErrorCode kod, String komunikat, HttpServletRequest req) {
        Map<String, Object> m = new HashMap<>();
        m.put("kod", kod.name());
        m.put("status", kod.getHttpStatus().value());
        m.put("komunikat", (komunikat == null || komunikat.isBlank()) ? kod.getDomyslnyKomunikat() : komunikat);
        m.put("sciezka", req.getRequestURI());
        m.put("czas", Instant.now().toString());
        return m;
    }
}
