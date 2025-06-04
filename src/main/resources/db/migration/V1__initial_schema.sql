drop table if exists asked_questions;
drop table if exists interviews;
drop table if exists questions;
drop table if exists levels;
drop table if exists topics;
drop table if exists users;

create table levels
(
    id           bigserial    not null primary key,
    name         varchar(50)  not null,
    description  varchar(200)
);

create table users
(
    id           bigserial   not null primary key,
    tg_firstname varchar(50) not null,
    tg_lastname  varchar(50),
    tg_username  varchar(50),
    mobile_phone varchar(20)
);

create table topics
(
    id           bigserial   not null primary key,
    topic        varchar(50) not null
);

create table questions
(
    id           bigserial    not null primary key,
    topic_id     bigint       not null references topics(id),
    level_id     bigint       not null references levels(id),
    question     varchar(200) not null
);

create table interviews
(
    id             bigserial     not null primary key,
    user_id        bigint        not null references users(id),
    date           timestamp     not null,
    level_id       bigint        not null references levels(id),
    feedback       varchar(4000)  not null,
    result_grade   integer
);

create table asked_questions
(
    id             bigserial     not null primary key,
    interview_id   bigint        not null references interviews(id),
    used_question  bigint        not null references questions(id),
    question_text  varchar(1000)  not null,
    answer_text    varchar(500)  not null
);
