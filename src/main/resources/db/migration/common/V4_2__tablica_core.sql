-- datasource: whiteboard_db@localhost
-- schema: whiteboard_db
CREATE TABLE IF NOT EXISTS tablica (
                                       id            BIGINT       NOT NULL AUTO_INCREMENT,
                                       tytul         VARCHAR(255) NOT NULL,
                                       wlasciciel_id BIGINT       NOT NULL,
                                       utworzono     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       zmodyfikowano TIMESTAMP    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                       CONSTRAINT pk_tablica PRIMARY KEY (id)
);
