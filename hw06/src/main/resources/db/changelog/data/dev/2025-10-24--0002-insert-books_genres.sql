--liquibase formatted sql

--changeset ds.rogv:2025-10-24--0002-insert-books_genres context:dev

insert into books_genres(book_id, genre_id)
values (1, 1),   (1, 2),
       (2, 3),   (2, 4),
       (3, 5),   (3, 6);
