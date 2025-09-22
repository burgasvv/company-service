
--liquibase formatted sql

--changeset burgasvv:1
begin ;
insert into company(name, description) values ('Teleset', 'Описание и история компании Teleset');
insert into company(name, description) values ('Computer Science', 'Описание и история компании Computer Science');
commit ;