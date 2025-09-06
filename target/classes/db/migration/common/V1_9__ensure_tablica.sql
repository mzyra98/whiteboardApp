-- datasource: whiteboard_db@localhost
-- schema: whiteboard_db
CREATE TABLE IF NOT EXISTS tablica (
                                       id BIGINT NOT NULL AUTO_INCREMENT,
                                       nazwa VARCHAR(255) NOT NULL,
                                       utworzono DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       zmodyfikowano DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                       PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
