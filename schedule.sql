create table author
(
    author_id  bigint       not null auto_increment,
    name       varchar(15)  not null,
    email      varchar(255) not null,
    created_at timestamp    not null,
    updated_at timestamp    not null,
    primary key (author_id)
);

create table schedule
(
    schedule_id bigint(20)   not null auto_increment,
    author_id   bigint(20)   not null,
    password    int(11)      not null,
    todo        varchar(255) not null,
    created_at  timestamp    not null,
    updated_at  timestamp    not null,
    primary key (schedule_id),
    constraint foreign key (author_id)
        references author (author_id)
);
