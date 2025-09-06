-- datasource: whiteboard_db@localhost
-- schema: whiteboard_db
CREATE TABLE udostepnienia (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               tablica_id BIGINT NOT NULL,
                               uzytkownik_id BIGINT NOT NULL,
                               data_utworzenia TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               data_wygasniecia TIMESTAMP NULL,
                               uprawnienia VARCHAR(50) NOT NULL
);
