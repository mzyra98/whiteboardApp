-- datasource: whiteboard_db@localhost
-- schema: whiteboard_db
create table if not exists users (
                                     id    bigint primary key auto_increment,
                                     email varchar(255)
);
create table if not exists tablice (
                                       id      bigint primary key auto_increment,
                                       nazwa   varchar(255),
                                       utworzona timestamp default current_timestamp not null
);
