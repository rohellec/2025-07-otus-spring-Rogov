--liquibase formatted sql

--changeset ds.rogv:2025-10-25--0001-insert-comments context:dev

insert into comments(text, book_id)
values ('Comment_1', 1), ('Comment_2', 1),
       ('Comment_3', 2), ('Comment_4', 2),
       ('Comment_5', 3), ('Comment_6', 3);
