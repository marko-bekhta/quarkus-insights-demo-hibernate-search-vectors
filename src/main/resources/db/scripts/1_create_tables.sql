create sequence public.book_seq increment by 50;
create sequence public.author_seq increment by 50;

create table public.author
(
    id   bigint not null primary key,
    name varchar(512)
);


create table public.book
(
    author_id      bigint
        constraint fk_author references public.author,
    id             bigint not null primary key,
    coverlocation  text,
    summary        text,
    title          text,
    coverembedding real[]
);

alter table public.book
    owner to quarkus;

create table public.book_genres
(
    genres  smallint
        constraint book_genres_genres_check check ((genres >= 0) AND (genres <= 13)),
    book_id bigint not null
        constraint fk_book references public.book
);

alter table public.book_genres
    owner to quarkus;

