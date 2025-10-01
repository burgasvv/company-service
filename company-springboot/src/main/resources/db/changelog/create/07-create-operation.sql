
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists operation (
    id uuid default gen_random_uuid() unique not null ,
    operation_type varchar not null ,
    amount decimal not null default 0 check ( amount >= 0 ) ,
    sender_wallet_id uuid references wallet(id) on delete cascade on update cascade ,
    receiver_wallet_id uuid references wallet(id) on delete cascade on update cascade ,
    created_at timestamp not null
)