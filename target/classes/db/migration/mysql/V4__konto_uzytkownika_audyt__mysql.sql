-- datasource: whiteboard_db@localhost
-- schema: whiteboard_db
CREATE TABLE IF NOT EXISTS konto_uzytkownika_audyt (
                                                       id          BIGINT       NOT NULL AUTO_INCREMENT,
                                                       konto_id    BIGINT       NOT NULL,
                                                       akcja       VARCHAR(16)  NOT NULL,
                                                       stare       LONGTEXT     NULL,
                                                       nowe        LONGTEXT     NULL,
                                                       autor       VARCHAR(320) NULL,
                                                       utworzono   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                       CONSTRAINT pk_kua PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_kua_konto ON konto_uzytkownika_audyt(konto_id);
CREATE INDEX idx_kua_czas  ON konto_uzytkownika_audyt(utworzono);
