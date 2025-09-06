package pl.tablica.wbapp.usluga.realizacja;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.dto.StrokeExportDto;
import pl.tablica.wbapp.dto.TablicaExportDto;
import pl.tablica.wbapp.konfiguracja.WlasciwosciLimitowTablic;
import pl.tablica.wbapp.model.KontoUzytkownika;
import pl.tablica.wbapp.model.Pociagniecie;
import pl.tablica.wbapp.model.Tablica;

@Service
@Transactional
public class UslugaImportuIEksportuDomyslneUstawienia implements UslugaImportuIEksportu {
    private static final String SCHEMA = "pl.tablica/board-export";
    private static final int VERSION = 1;

    private static final Logger log = LoggerFactory.getLogger(UslugaImportuIEksportuDomyslneUstawienia.class);

    @PersistenceContext
    private EntityManager em;

    private final WlasciwosciLimitowTablic limity;

    public UslugaImportuIEksportuDomyslneUstawienia(WlasciwosciLimitowTablic limity) {
        this.limity = limity;
    }

    @Override
    public TablicaExportDto eksportuj(Long tablicaId, String emailZglaszajacego) {
        var zglaszajacy = znajdzKontoPoEmailu(emailZglaszajacego);
        if (zglaszajacy == null) throw new IllegalArgumentException("Brak konta: " + emailZglaszajacego);

        var tablica = em.find(Tablica.class, tablicaId);
        if (tablica == null) throw new IllegalArgumentException("Nie znaleziono tablicy: " + tablicaId);

        var wlasciciel = (KontoUzytkownika) getProp(tablica, "getWlasciciel", "wlasciciel");
        var wlascicielEmail = wlasciciel != null ? (String) getProp(wlasciciel, "getEmail", "email") : null;

        if (!Objects.equals(normalize(wlascicielEmail), normalize(zglaszajacy.getEmail())) && !maRoleAdministratora(zglaszajacy)) {
            throw new AccessDeniedException("Brak uprawnień do eksportu tablicy: " + tablicaId);
        }

        var tytul = (String) getProp(tablica, "getTytul", "tytul");
        var utworzono = (Instant) getAny(tablica,
                new String[]{"getUtworzono","getCreatedAt"},
                new String[]{"utworzono","createdAt"});
        var zmodyfikowano = (Instant) getAny(tablica,
                new String[]{"getZmodyfikowano","getUpdatedAt"},
                new String[]{"zmodyfikowano","updatedAt"});

        var tabIdObj = getProp(tablica, "getId", "id");
        Long tabId = null;
        if (tabIdObj instanceof Number n) tabId = n.longValue();
        if (tabId == null) throw new IllegalStateException("Brak identyfikatora tablicy");

        var pociagniecia = em.createQuery(
                "select p from Pociagniecie p where p.tablica.id = :tid order by p.id asc",
                Pociagniecie.class
        ).setParameter("tid", tabId).getResultList();

        var strokeDtos = new ArrayList<StrokeExportDto>(pociagniecia.size());
        for (var p : pociagniecia) {
            var autor = (KontoUzytkownika) getProp(p, "getAutor", "autor");
            var autorEmail = autor != null ? (String) getProp(autor, "getEmail", "email") : null;

            String kolor = (String) getProp(p, "getKolor", "kolor");
            Integer grubosc = (Integer) getProp(p, "getGrubosc", "grubosc");
            @SuppressWarnings("unchecked")
            List<List<Integer>> punkty = (List<List<Integer>>) getProp(p, "getPunkty", "punkty");

            Instant czas = null;
            Object czasRaw = getProp(p, "getCzas", "czas");
            if (czasRaw instanceof Instant ci) czas = ci;
            else if (czasRaw instanceof LocalDateTime ldt) czas = ldt.toInstant(ZoneOffset.UTC);

            strokeDtos.add(new StrokeExportDto(
                    (Long) getProp(p, "getId", "id"),
                    autorEmail,
                    kolor,
                    grubosc,
                    punkty,
                    czas
            ));
        }

        return new TablicaExportDto(
                SCHEMA,
                VERSION,
                (Long) getProp(tablica, "getId", "id"),
                tytul,
                wlascicielEmail,
                utworzono,
                zmodyfikowano,
                strokeDtos
        );
    }

    @Override
    public Long importuj(TablicaExportDto dto, String emailZglaszajacego) {
        if (dto == null) throw new IllegalArgumentException("Brak danych importu");
        if (!SCHEMA.equals(dto.schema())) throw new IllegalArgumentException("Nieobsługiwany schema");
        if (dto.version() != VERSION) throw new IllegalArgumentException("Nieobsługiwana wersja schematu");

        var zglaszajacy = znajdzKontoPoEmailu(emailZglaszajacego);
        if (zglaszajacy == null) throw new IllegalArgumentException("Brak konta: " + emailZglaszajacego);

        Integer maks = pobierzLimitMaksTablicNaUzytkownika();
        if (maks != null) {
            try {
                long ile = em.createQuery(
                        "select count(t) from Tablica t join t.wlasciciel w where lower(w.email) = lower(:e)",
                        Long.class
                ).setParameter("e", zglaszajacy.getEmail()).getSingleResult();
                if (ile >= maks) throw new IllegalStateException("Przekroczono limit liczby tablic dla użytkownika");
            } catch (Exception ex) {
                log.warn("Nie udało się policzyć tablic użytkownika, pomijam limit na czas importu: {}", ex.getMessage());
            }
        }

        var teraz = Instant.now();

        var nowa = new Tablica();
        setAnySmart(nowa, new String[]{"setTytul"}, new String[]{"tytul"}, dto.tytul());
        setAnySmart(nowa, new String[]{"setWlasciciel"}, new String[]{"wlasciciel"}, zglaszajacy);
        setAnySmart(nowa, new String[]{"setUtworzono","setCreatedAt"}, new String[]{"utworzono","createdAt"}, teraz);
        setAnySmart(nowa, new String[]{"setZmodyfikowano","setUpdatedAt"}, new String[]{"zmodyfikowano","updatedAt"}, teraz);
        em.persist(nowa);
        em.flush();

        var lista = dto.strokes() != null ? dto.strokes() : List.<StrokeExportDto>of();
        for (var se : lista) {
            var autor = se.autorEmail() != null ? znajdzKontoPoEmailu(se.autorEmail()) : null;
            if (autor == null) autor = zglaszajacy;

            var p = new Pociagniecie();
            setAnySmart(p, new String[]{"setTablica"}, new String[]{"tablica"}, nowa);
            setAnySmart(p, new String[]{"setAutor"}, new String[]{"autor"}, autor);

            String domyslnyTyp = "FREEHAND";
            String daneJson = "{\"kolor\":\"" + nullSafe(se.kolor()) + "\",\"grubosc\":" + nullSafe(se.grubosc()) +
                    ",\"punkty\":" + (se.punkty()==null ? "[]" : se.punkty().toString()) + "}";

            setAnySmart(p, new String[]{"setTyp"}, new String[]{"typ"}, domyslnyTyp);
            setAnySmart(p, new String[]{"setDane"}, new String[]{"dane"}, daneJson);

            Instant src = se.czas() != null ? se.czas() : teraz;
            setAnySmart(p, new String[]{"setCzas"}, new String[]{"czas"}, src);

            setAnySmart(p, new String[]{"setKolor"}, new String[]{"kolor"}, se.kolor());
            setAnySmart(p, new String[]{"setGrubosc"}, new String[]{"grubosc"}, se.grubosc());
            setAnySmart(p, new String[]{"setPunkty"}, new String[]{"punkty"}, se.punkty());

            em.persist(p);
        }
        em.flush();
        return (Long) getProp(nowa, "getId", "id");
    }

    private KontoUzytkownika znajdzKontoPoEmailu(String email) {
        try {
            return em.createQuery(
                    "select k from KontoUzytkownika k where lower(k.email) = lower(:e)",
                    KontoUzytkownika.class
            ).setParameter("e", email).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    private boolean maRoleAdministratora(KontoUzytkownika konto) {
        var r = String.valueOf(getProp(konto, "getRola", "rola"));
        return "ADMIN".equalsIgnoreCase(r);
    }

    private Integer pobierzLimitMaksTablicNaUzytkownika() {
        Object v = tryProp(limity,
                new String[]{"getMaksTablicNaUzytkownika","getMaksTablic","getMaxTablicNaUzytkownika","getMaxTablic"},
                new String[]{"maksTablicNaUzytkownika","maksTablic","maxTablicNaUzytkownika","maxTablic"});
        if (v instanceof Number n) return n.intValue();
        return null;
    }

    private static Object tryProp(Object target, String[] getters, String[] fields) {
        if (target == null) return null;
        for (var g : getters) {
            try {
                Method m = target.getClass().getMethod(g);
                return m.invoke(target);
            } catch (Exception ignore) {}
        }
        for (var f : fields) {
            try {
                Field ff = target.getClass().getDeclaredField(f);
                ff.setAccessible(true);
                return ff.get(target);
            } catch (Exception ignore) {}
        }
        return null;
    }

    private static Object getProp(Object target, String getterName, String fieldName) {
        if (target == null) return null;
        try {
            Method m = target.getClass().getMethod(getterName);
            return m.invoke(target);
        } catch (Exception ignore) {
            try {
                Field f = target.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.get(target);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private static Object getAny(Object target, String[] getters, String[] fields) {
        return tryProp(target, getters, fields);
    }

    private static void setAnySmart(Object target, String[] setters, String[] fields, Object value) {
        if (target == null) return;

        for (var name : setters) {
            Method m = findSetter(target.getClass(), name);
            if (m != null) {
                try {
                    Object coerced = coerce(value, m.getParameterTypes()[0]);
                    m.invoke(target, coerced);
                    return;
                } catch (Exception ignore) {}
            }
        }
        for (var fName : fields) {
            try {
                Field f = target.getClass().getDeclaredField(fName);
                f.setAccessible(true);
                Object coerced = coerce(value, f.getType());
                f.set(target, coerced);
                return;
            } catch (Exception ignore) { }
        }
    }

    private static Method findSetter(Class<?> cls, String name) {
        for (Method m : cls.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 1) {
                return m;
            }
        }
        return null;
    }

    private static Object coerce(Object value, Class<?> toType) {
        if (value == null) {
            if (toType.isEnum()) {
                Object[] constants = toType.getEnumConstants();
                return constants != null && constants.length > 0 ? constants[0] : null;
            }
            if (toType == String.class) return "";
            if (toType == Integer.class || toType == int.class) return 0;
            if (toType == Long.class || toType == long.class) return 0L;
            if (toType == Instant.class) return Instant.now();
            if (toType == LocalDateTime.class) return LocalDateTime.now(ZoneOffset.UTC);
            return null;
        }

        if (toType.isInstance(value)) return value;

        if (toType.isEnum()) {
            String s = String.valueOf(value);
            Object[] constants = toType.getEnumConstants();
            if (constants != null) {
                for (Object c : constants) {
                    if (c.toString().equalsIgnoreCase(s)) return c;
                }
                return constants[0];
            }
        }

        if (toType == String.class) return String.valueOf(value);

        if ((toType == Integer.class || toType == int.class) && value instanceof Number n) return n.intValue();
        if ((toType == Long.class || toType == long.class) && value instanceof Number n2) return n2.longValue();

        if (toType == Integer.class || toType == int.class) return Integer.parseInt(String.valueOf(value));
        if (toType == Long.class || toType == long.class) return Long.parseLong(String.valueOf(value));

        if (toType == Instant.class && value instanceof LocalDateTime ldt) return ldt.toInstant(ZoneOffset.UTC);
        if (toType == LocalDateTime.class && value instanceof Instant i) return LocalDateTime.ofInstant(i, ZoneOffset.UTC);

        return value;
    }

    private static String nullSafe(Object v) { return v == null ? "" : String.valueOf(v); }
    private static String normalize(String s) { return s == null ? null : s.toLowerCase(Locale.ROOT).trim(); }
}
