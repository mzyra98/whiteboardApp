drop table if exists udostepnienia;

create table if not exists linki_udostepnienia (
                                                   id bigint primary key auto_increment,
                                                   tablica_id bigint not null,
                                                   tworca_uzytkownik_id bigint not null,
                                                   uprawnienie varchar(16) not null,
                                                   token varchar(64) not null unique,
                                                   wygasa timestamp null,
                                                   maks_wejsc int null,
                                                   liczba_wejsc int null,
                                                   anulowany boolean not null default false,
                                                   utworzony timestamp not null,
                                                   constraint fk_lu_tablica foreign key (tablica_id) references tablice(id),
                                                   constraint fk_lu_tworca foreign key (tworca_uzytkownik_id) references users(id)
);

create index idx_lu_tablica on linki_udostepnienia(tablica_id);
create index idx_lu_token  on linki_udostepnienia(token);

create table if not exists wspolpracownicy_tablicy (
                                                       id bigint primary key auto_increment,
                                                       tablica_id bigint not null,
                                                       uzytkownik_id bigint not null,
                                                       uprawnienie varchar(16) not null,
                                                       tymczasowy boolean not null,
                                                       constraint uq_tablica_uzytkownik unique (tablica_id, uzytkownik_id),
                                                       constraint fk_wt_tablica foreign key (tablica_id) references tablice(id),
                                                       constraint fk_wt_uzytkownik foreign key (uzytkownik_id) references users(id)
);

create index idx_wt_tablica    on wspolpracownicy_tablicy(tablica_id);
create index idx_wt_uzytkownik on wspolpracownicy_tablicy(uzytkownik_id);

