ALTER TABLE strokes ADD IF NOT EXISTS autor_id BIGINT;
ALTER TABLE strokes ADD IF NOT EXISTS czas TIMESTAMP;
ALTER TABLE strokes ADD IF NOT EXISTS dane CLOB;
ALTER TABLE strokes ADD IF NOT EXISTS typ VARCHAR(50);
ALTER TABLE strokes ADD IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE strokes ADD IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE strokes
    ADD CONSTRAINT IF NOT EXISTS fk_strokes_autor
    FOREIGN KEY (autor_id) REFERENCES konto_uzytkownika(id);

ALTER TABLE strokes
    ADD CONSTRAINT IF NOT EXISTS fk_strokes_tablica
    FOREIGN KEY (tablica_id) REFERENCES tablice(id);

CREATE INDEX IF NOT EXISTS idx_strokes_tablica_id ON strokes(tablica_id);
CREATE INDEX IF NOT EXISTS idx_strokes_autor_id ON strokes(autor_id);
