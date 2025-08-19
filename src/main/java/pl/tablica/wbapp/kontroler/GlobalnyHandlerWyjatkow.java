package pl.tablica.wbapp.kontroler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pl.tablica.wbapp.udostepnianie.wyjatek.NieZnaleziono;
import pl.tablica.wbapp.udostepnianie.wyjatek.Wygasl;
import pl.tablica.wbapp.udostepnianie.wyjatek.ZabronioneWywolanie;
import pl.tablica.wbapp.udostepnianie.wyjatek.ZbytWieleProb;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalnyHandlerWyjatkow {

    private Map<String, Object> body(HttpStatus status, ErrorCode code, String detail, String path) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("timestamp", OffsetDateTime.now().toString());
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("code", code.code);
        m.put("message", code.message);
        if (detail != null && !detail.isBlank()) {
            m.put("detail", detail);
        }
        m.put("path", path);
        return m;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> onMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                        HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        Map<String, Object> m = body(st, ErrorCode.BLEDNE_DANE_WEJSCIOWE, ex.getMessage(), req.getRequestURI());
        List<Map<String, Object>> errs = ex.getBindingResult().getFieldErrors().stream().map(fe -> {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("field", fe.getField());
            e.put("rejectedValue", fe.getRejectedValue());
            e.put("message", fe.getDefaultMessage());
            return e;
        }).toList();
        m.put("errors", errs);
        return ResponseEntity.status(st).contentType(MediaType.APPLICATION_JSON).body(m);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> onConstraintViolation(ConstraintViolationException ex,
                                                                     HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        Map<String, Object> m = body(st, ErrorCode.NARUSZENIE_OGRANICZEN, ex.getMessage(), req.getRequestURI());
        List<Map<String, Object>> errs = ex.getConstraintViolations().stream().map(cv -> {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("property", cv.getPropertyPath().toString());
            e.put("invalidValue", cv.getInvalidValue());
            e.put("message", cv.getMessage());
            return e;
        }).toList();
        m.put("errors", errs);
        return ResponseEntity.status(st).contentType(MediaType.APPLICATION_JSON).body(m);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> onMissingParam(MissingServletRequestParameterException ex,
                                                              HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        String detail = "Brak parametru: " + ex.getParameterName();
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.BRAK_PARAMETRU, detail, req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> onTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                              HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        String detail = "Zły typ parametru: " + ex.getName();
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.ZLY_TYP_PARAMETRU, detail, req.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> onNotReadable(HttpMessageNotReadableException ex,
                                                             HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        ex.getMostSpecificCause();
        String detail = ex.getMostSpecificCause().getMessage();
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.NIEPRAWIDLOWY_ARGUMENT, detail, req.getRequestURI()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> onMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                                                                  HttpServletRequest req) {
        HttpStatus st = HttpStatus.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.NIEDOZWOLONA_METODA, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> onNotFound(RuntimeException ex,
                                                          HttpServletRequest req) {
        HttpStatus st = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.NIE_ZNALEZIONO, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(NieZnaleziono.class)
    public ResponseEntity<Map<String, Object>> onCustomNotFound(NieZnaleziono ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.NIE_ZNALEZIONO, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(Wygasl.class)
    public ResponseEntity<Map<String, Object>> onGone(Wygasl ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.GONE;
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.WYGASL, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(ZabronioneWywolanie.class)
    public ResponseEntity<Map<String, Object>> onForbidden(ZabronioneWywolanie ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.ZABRONIONE_WYWOLANIE, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(ZbytWieleProb.class)
    public ResponseEntity<Map<String, Object>> onTooManyRequests(ZbytWieleProb ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.TOO_MANY_REQUESTS;
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.ZA_DUZO_ZADAN, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> onIllegalArgument(IllegalArgumentException ex,
                                                                 HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.NIEPRAWIDLOWY_ARGUMENT, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> onAny(Exception ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(st)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body(st, ErrorCode.BLAD_WEWNETRZNY, ex.getMessage(), req.getRequestURI()));
    }
}