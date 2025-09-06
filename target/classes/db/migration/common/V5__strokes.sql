-- datasource: whiteboard_db@localhost
-- schema: whiteboard_db
CREATE TABLE IF NOT EXISTS strokes (
                                       id                   BIGINT       NOT NULL AUTO_INCREMENT,
                                       tablica_id           BIGINT       NOT NULL,
                                       konto_uzytkownika_id BIGINT       NOT NULL,
                                       typ                  VARCHAR(32)  NOT NULL DEFAULT 'pen',
                                       dane                 JSON         NOT NULL,
                                       utworzono            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                       zmodyfikowano        DATETIME(6)  NULL     DEFAULT NULL
                                           ON UPDATE CURRENT_TIMESTAMP(6),

                                       INDEX idx_strokes_tablica (tablica_id),
                                       INDEX idx_strokes_autor   (konto_uzytkownika_id),

                                       CONSTRAINT fk_strokes_tablica
                                           FOREIGN KEY (tablica_id) REFERENCES tablica(id) ON DELETE CASCADE,

                                       CONSTRAINT fk_strokes_autor
                                           FOREIGN KEY (konto_uzytkownika_id) REFERENCES konto_uzytkownika(id) ON DELETE CASCADE,

                                       PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
