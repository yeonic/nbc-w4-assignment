create table user
(
    user_id    bigint auto_increment
        primary key,
    name       varchar(15)                         not null,
    email      varchar(255)                        not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
);

create table schedule
(
    schedule_id bigint auto_increment
        primary key,
    user_id     bigint                              not null,
    password    varchar(80)                         null,
    todo        varchar(255)                        not null,
    created_at  timestamp default CURRENT_TIMESTAMP not null,
    updated_at  timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint schedule_ibfk_1
        foreign key (user_id) references user (user_id)
);

create index user_id
    on schedule (user_id);

