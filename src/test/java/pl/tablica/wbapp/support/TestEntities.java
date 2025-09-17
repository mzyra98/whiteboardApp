package pl.tablica.wbapp.support;

import java.lang.reflect.Field;

public final class TestEntities {
    private TestEntities() {}

    public static <T> T set(Object target, String field, Object value) {
        try {
            Field f = find(target.getClass(), field);
            if (f == null) throw new NoSuchFieldException(field);
            f.setAccessible(true);
            f.set(target, value);
            @SuppressWarnings("unchecked") T t = (T) target;
            return t;
        } catch (Exception e) {
            throw new IllegalStateException("Nie można ustawić pola '" + field + "' w " + target.getClass(), e);
        }
    }

    public static <T> T setIfExists(Object target, String field, Object value) {
        try {
            Field f = find(target.getClass(), field);
            if (f != null) {
                f.setAccessible(true);
                f.set(target, value);
            }
            @SuppressWarnings("unchecked") T t = (T) target;
            return t;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Field find(Class<?> type, String name) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try { return c.getDeclaredField(name); }
            catch (NoSuchFieldException ignore) {}
        }
        return null;
    }
}
