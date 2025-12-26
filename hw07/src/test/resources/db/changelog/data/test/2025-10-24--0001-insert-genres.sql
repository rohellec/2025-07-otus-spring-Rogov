--liquibase formatted sql

--changeset ds.rogv:2025-10-24--0001-insert-genres context:test

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3'),
       ('Genre_4'), ('Genre_5'), ('Genre_6');
