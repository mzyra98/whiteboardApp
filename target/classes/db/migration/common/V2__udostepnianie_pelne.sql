-- datasource: whiteboard_db@localhost
-- schema: whiteboard_db
CREATE TABLE IF NOT EXISTS udostepnienie (
                                             id                    BIGINT NOT NULL AUTO_INCREMENT,
                                             tablica_id            BIGINT NOT NULL,
                                             konto_uzytkownika_id  BIGINT NOT NULL,
                                             uprawnienie ENUM('OWNER','EDIT','VIEW') NOT NULL,
                                             utworzono             DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                                             PRIMARY KEY (id),
                                             UNIQUE KEY uk_udostepnienie_tablica_uzytkownik (tablica_id, konto_uzytkownika_id),

                                             CONSTRAINT fk_udostepnienie__tablica
                                                 FOREIGN KEY (tablica_id) REFERENCES tablica (id) ON DELETE CASCADE,
                                             CONSTRAINT fk_udostepnienie__konto
                                                 FOREIGN KEY (konto_uzytkownika_id) REFERENCES konto_uzytkownika (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS link_udostepnienia (
                                                  id           BIGINT NOT NULL AUTO_INCREMENT,
                                                  tablica_id   BIGINT NOT NULL,
                                                  token        CHAR(36) NOT NULL,
                                                  tryb         ENUM('VIEW','EDIT') NOT NULL DEFAULT 'VIEW',
                                                  wygasa_do    DATETIME(6) NULL,
                                                  utworzono    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                                                  PRIMARY KEY (id),
                                                  UNIQUE KEY uk_link_token (token),

                                                  CONSTRAINT fk_link__tablica
                                                      FOREIGN KEY (tablica_id) REFERENCES tablica (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;