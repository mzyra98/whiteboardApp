-- datasource: whiteboard_db@localhost
-- schema: whiteboard_db
CREATE TABLE IF NOT EXISTS konto_uzytkownika (
                                                 id                 BIGINT NOT NULL AUTO_INCREMENT,
                                                 email              VARCHAR(320) NOT NULL,
                                                 haslo_hash         VARCHAR(100) NOT NULL,
                                                 nazwa_wyswietlana  VARCHAR(100) NOT NULL,
                                                 rola               VARCHAR(20)  NOT NULL,
                                                 aktywny            BOOLEAN      NOT NULL DEFAULT TRUE,
                                                 created_at         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                                 updated_at         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                                                 CONSTRAINT pk_konto_uzytkownika PRIMARY KEY (id),
                                                 CONSTRAINT uk_konto_uzytkownika_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;