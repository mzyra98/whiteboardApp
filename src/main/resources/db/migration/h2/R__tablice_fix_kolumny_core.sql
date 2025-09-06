ALTER TABLE tablice ADD IF NOT EXISTS tytul VARCHAR(255);

ALTER TABLE tablice ADD IF NOT EXISTS wlasciciel_id BIGINT;

ALTER TABLE tablice
    ADD CONSTRAINT IF NOT EXISTS fk_tablice_wlasciciel
    FOREIGN KEY (wlasciciel_id) REFERENCES konto_uzytkownika(id);

CREATE INDEX IF NOT EXISTS idx_tablice_wlasciciel_id ON tablice(wlasciciel_id);

ALTER TABLE tablice ADD IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE tablice ADD IF NOT EXISTS updated_at TIMESTAMP;
