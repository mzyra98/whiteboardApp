-- datasource: whiteboard_db@localhost
-- schema: whiteboard_db
CREATE TABLE IF NOT EXISTS konto_uzytkownika (
                                                 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                 email VARCHAR(320) NOT NULL UNIQUE,
                                                 haslo_hash VARCHAR(100) NOT NULL,
                                                 nazwa_wyswietlana VARCHAR(100) NOT NULL,
                                                 rola VARCHAR(20) NOT NULL,
                                                 aktywny BOOLEAN NOT NULL DEFAULT TRUE
);
