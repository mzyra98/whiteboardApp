package pl.tablica.wbapp.usluga.realizacja;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tablica.wbapp.usluga.UslugaRenderowaniaTablicy;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class UslugaRenderowaniaTablicyDomyslneUstawienia implements UslugaRenderowaniaTablicy {

    @PersistenceContext
    private EntityManager em;

    @Override
    public byte[] renderujPng(Long tablicaId, Integer szerokosc, Integer wysokosc) {
        int w = (szerokosc != null && szerokosc > 0) ? szerokosc : 1600;
        int h = (wysokosc != null && wysokosc > 0) ? wysokosc : 900;

        var strokes = pobierzPociagniecia(tablicaId);

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (var s : strokes) {
            g.setColor(s.kolor());
            g.setStroke(new BasicStroke(s.grubosc(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            var pts = s.punkty();
            for (int i = 1; i < pts.size(); i++) {
                var p1 = pts.get(i - 1);
                var p2 = pts.get(i);
                g.drawLine(p1[0], p1[1], p2[0], p2[1]);
            }
        }
        g.dispose();

        try (var baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Nie udało się wyrenderować PNG", e);
        }
    }

    @Override
    public byte[] renderujPdf(Long tablicaId, Integer szerokosc, Integer wysokosc) {
        int w = (szerokosc != null && szerokosc > 0) ? szerokosc : 1600;
        int h = (wysokosc != null && wysokosc > 0) ? wysokosc : 900;

        var strokes = pobierzPociagniecia(tablicaId);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(w, h));
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.setNonStrokingColor(1f, 1f, 1f);
                cs.addRect(0, 0, w, h);
                cs.fill();

                for (var s : strokes) {
                    cs.setStrokingColor(
                            s.kolor().getRed() / 255f,
                            s.kolor().getGreen() / 255f,
                            s.kolor().getBlue() / 255f
                    );
                    cs.setLineWidth(Math.max(1f, s.grubosc()));
                    var pts = s.punkty();
                    if (pts.size() >= 2) {
                        var first = pts.getFirst();
                        cs.moveTo(first[0], h - first[1]);
                        for (int i = 1; i < pts.size(); i++) {
                            var p = pts.get(i);
                            cs.lineTo(p[0], h - p[1]);
                        }
                        cs.stroke();
                    }
                }
            }

            try (var baos = new ByteArrayOutputStream()) {
                doc.save(baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Nie udało się wyrenderować PDF", e);
        }
    }

    private record Rysunek(int grubosc, Color kolor, List<int[]> punkty, Instant czas) {}

    private List<Rysunek> pobierzPociagniecia(Long tablicaId) {
        var lista = em.createQuery(
                "select p from Pociagniecie p where p.tablica.id = :id order by p.id asc",
                Object.class
        ).setParameter("id", tablicaId).getResultList();

        List<Rysunek> wynik = new ArrayList<>();
        for (var obj : lista) {
            int grubosc = odczytajGrubosc(obj);
            Color kolor = odczytajKolor(obj);
            List<int[]> pkt = odczytajPunkty(obj);
            Instant czas = odczytajCzas(obj);
            wynik.add(new Rysunek(grubosc, kolor, pkt, czas));
        }
        return wynik;
    }

    private int odczytajGrubosc(Object p) {
        Object v = wartosc(p, "getGrubosc", "grubosc");
        if (v instanceof Number n) return n.intValue();
        String dane = odczytajDaneJson(p);
        if (dane != null) {
            try {
                Map<?, ?> m = new com.fasterxml.jackson.databind.ObjectMapper().readValue(dane, Map.class);
                Object g = m.get("grubosc");
                if (g instanceof Number n) return n.intValue();
                if (g != null) return Integer.parseInt(g.toString());
            } catch (Exception ignore) {}
        }
        return 3;
    }

    private Color odczytajKolor(Object p) {
        Object v = wartosc(p, "getKolor", "kolor");
        if (v != null) {
            Color c = kolorZStringa(v.toString());
            if (c != null) return c;
        }
        String dane = odczytajDaneJson(p);
        if (dane != null) {
            try {
                Map<?, ?> m = new com.fasterxml.jackson.databind.ObjectMapper().readValue(dane, Map.class);
                Object k = m.get("kolor");
                if (k != null) {
                    Color c = kolorZStringa(k.toString());
                    if (c != null) return c;
                }
            } catch (Exception ignore) {}
        }
        return Color.BLACK;
    }

    private List<int[]> odczytajPunkty(Object p) {
        Object v = wartosc(p, "getPunkty", "punkty");
        if (v instanceof List<?> l) return mapujPunkty(l);

        String dane = odczytajDaneJson(p);
        if (dane != null) {
            try {
                Map<?, ?> m = new com.fasterxml.jackson.databind.ObjectMapper().readValue(dane, Map.class);
                Object arr = m.get("punkty");
                if (arr instanceof List<?> l) return mapujPunkty(l);
            } catch (Exception ignore) {}
        }
        return List.of();
    }

    private static List<int[]> mapujPunkty(List<?> l) {
        List<int[]> wynik = new ArrayList<>(l.size());
        for (Object el : l) {
            if (el instanceof List<?> pp && pp.size() >= 2) {
                int x = toInt(pp.get(0));
                int y = toInt(pp.get(1));
                wynik.add(new int[]{x, y});
            }
        }
        return wynik;
    }

    private Instant odczytajCzas(Object p) {
        Object v = wartosc(p, "getCzas", "czas");
        if (v instanceof Instant i) return i;
        return Instant.now();
    }

    private String odczytajDaneJson(Object p) {
        Object v = wartosc(p, "getDane", "dane");
        return v != null ? v.toString() : null;
    }

    private static int toInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(String.valueOf(o));
    }

    private static Color kolorZStringa(String s) {
        if (s == null || s.isBlank()) return null;
        String x = s.trim();
        if (x.startsWith("#")) {
            try {
                int rgb = Integer.parseInt(x.substring(1), 16);
                return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb) & 0xFF);
            } catch (Exception ignore) { return null; }
        }
        try {
            return (Color) Color.class.getField(x.toUpperCase()).get(null);
        } catch (Exception ignore) { return null; }
    }

    private static Object wartosc(Object target, String getter, String pole) {
        try {
            var m = target.getClass().getMethod(getter);
            return m.invoke(target);
        } catch (Exception ignore) {
            try {
                var f = target.getClass().getDeclaredField(pole);
                f.setAccessible(true);
                return f.get(target);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
