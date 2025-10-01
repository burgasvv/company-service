
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists message (
    id uuid default gen_random_uuid() unique not null ,
    content text ,
    sender_id uuid references identity(id) on delete set null on update cascade ,
    chat_id uuid references chat(id) on delete cascade on update cascade ,
    created_at timestamp not null
);