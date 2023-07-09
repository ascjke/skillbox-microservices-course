-- create database users;
-- create schema users_scheme;
SET search_path TO users_scheme, public;

create table _user
(
    id            UUID default gen_random_uuid() not null,
    username      varchar(63)                    not null unique,
    password_hash varchar(255)                   not null,
    email         varchar(63)                    not null,
    last_name     varchar(63),
    first_name    varchar(63),
    middle_name   varchar(63),
    birth_date    date,
    male          varchar(7) check (male in ('MALE', 'FEMALE')),
    city          varchar(31),
    bio           varchar(511),
    phone         varchar(63),
    profile_image varchar(255),
    deleted       boolean,
    created_at    timestamp(6),
    updated_at    timestamp(6),
    primary key (id)
);

create table subscription
(
    id      UUID default gen_random_uuid() not null,
    user_id UUID,
    primary key (id)
);

create table following
(
    id      UUID default gen_random_uuid() not null,
    user_id UUID,
    primary key (id)
);

create table user_hard_skills
(
    user_id     UUID,
    hard_skills varchar(255)
);

alter table if exists subscription
    add constraint FKt2h2346u92r18ijwxfg6gir6k
        foreign key (user_id)
            references _user;

alter table if exists following
    add constraint FK4kpme8qo5uu1f7o71wk10lnxf
        foreign key (user_id)
            references _user;

alter table if exists user_hard_skills
    add constraint FKb069gscgsw2f14eq5iyr5p95j
        foreign key (user_id)
            references _user;