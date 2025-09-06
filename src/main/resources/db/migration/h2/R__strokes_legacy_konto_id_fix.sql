ALTER TABLE strokes ADD IF NOT EXISTS autor_id BIGINT;

UPDATE strokes
SET autor_id = konto_uzytkownika_id
WHERE autor_id IS NULL AND konto_uzytkownika_id IS NOT NULL;

ALTER TABLE strokes ALTER COLUMN konto_uzytkownika_id SET NULL;
