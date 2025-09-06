ALTER TABLE strokes ADD IF NOT EXISTS autor_id BIGINT;

ALTER TABLE strokes
    ADD CONSTRAINT IF NOT EXISTS fk_strokes_autor
    FOREIGN KEY (autor_id) REFERENCES konto_uzytkownika(id);

CREATE INDEX IF NOT EXISTS idx_strokes_autor_id ON strokes(autor_id);

ALTER TABLE strokes ADD IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE strokes ADD IF NOT EXISTS updated_at TIMESTAMP;
