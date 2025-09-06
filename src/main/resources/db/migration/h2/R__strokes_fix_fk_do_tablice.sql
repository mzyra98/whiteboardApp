ALTER TABLE strokes DROP CONSTRAINT IF EXISTS fk_strokes_tablica;

ALTER TABLE strokes
    ADD CONSTRAINT fk_strokes_tablica
        FOREIGN KEY (tablica_id) REFERENCES tablice(id);
