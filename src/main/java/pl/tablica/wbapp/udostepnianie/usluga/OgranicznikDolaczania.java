package pl.tablica.wbapp.udostepnianie.usluga;

import org.springframework.stereotype.Component;
import pl.tablica.wbapp.udostepnianie.wyjatek.ZbytWieleProb;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OgranicznikDolaczania {

    private final int maksProbWOknie;
    private final long oknoMs;

    private final Map<String, Deque<Long>> okna = new ConcurrentHashMap<>();

    public OgranicznikDolaczania() {
        this(10, 60_000L);
    }

    public OgranicznikDolaczania(int maksProbWOknie, long oknoMs) {
        if (maksProbWOknie < 1) throw new IllegalArgumentException("maksProbWOknie");
        if (oknoMs < 1) throw new IllegalArgumentException("oknoMs");
        this.maksProbWOknie = maksProbWOknie;
        this.oknoMs = oknoMs;
    }

    public void wymagajPozwolenia(String klucz) {
        if (klucz == null || klucz.isBlank()) throw new IllegalArgumentException("klucz");
        final long teraz = System.currentTimeMillis();
        Deque<Long> q = okna.computeIfAbsent(klucz, k -> new ArrayDeque<>());
        synchronized (q) {
            while (!q.isEmpty() && (teraz - q.peekFirst()) > oknoMs) {
                q.removeFirst();
            }
            if (q.size() >= maksProbWOknie) {
                throw new ZbytWieleProb("Limit prób w oknie czasowym został przekroczony.");
            }
            q.addLast(teraz);
        }
    }

    public boolean czyDozwolone(String klucz) {
        try {
            wymagajPozwolenia(klucz);
            return true;
        } catch (ZbytWieleProb ex) {
            return false;
        }
    }
}
