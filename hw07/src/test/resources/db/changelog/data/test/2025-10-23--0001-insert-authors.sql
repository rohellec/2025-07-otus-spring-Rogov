--liquibase formatted sql

--changeset ds.rogv:2025-10-23--0001-insert-authors context:test

insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');
