--liquibase formatted sql

--changeset ds.rogv:2025-10-23--0002-insert-books context:dev

insert into books(title, author_id)
values ('BookTitle_1', 1), ('BookTitle_2', 2), ('BookTitle_3', 3);
