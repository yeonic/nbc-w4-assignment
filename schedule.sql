create table user
(
    user_id    bigint       not null auto_increment,
    name       varchar(15)  not null,
    email      varchar(255) not null,
    created_at timestamp    not null default current_timestamp,
    updated_at timestamp    not null default current_timestamp on update current_timestamp,
    primary key (user_id)
);

create table schedule
(
    schedule_id bigint(20)   not null auto_increment,
    author_id   bigint(20)   not null,
    password    int(11)      not null,
    todo        varchar(255) not null,
    created_at  timestamp    not null default current_timestamp,
    updated_at  timestamp    not null default current_timestamp on update current_timestamp,
    primary key (schedule_id),
    constraint foreign key (author_id)
        references user (user_id)
);
