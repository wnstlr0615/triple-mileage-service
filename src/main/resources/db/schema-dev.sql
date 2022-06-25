drop table if exists mileage;
drop table if exists mileage_history;

create table mileage
(
    id                 bigint auto_increment primary key,
    attached_photo_cnt int          not null,
    content_length     int          not null,
    created_at         datetime(6)  not null,
    deleted            bit          not null,
    deleted_at         datetime(6)  null,
    mileage_id         varchar(255) not null,
    place_id           varchar(255) not null,
    point              int          not null,
    review_id          varchar(255) not null,
    updated_at         datetime(6)  not null,
    user_id            varchar(255) not null
);

create index place_index
    on mileage (place_id);

create index place_review_user_index
    on mileage (place_id, review_id, user_id);


create table mileage_history
(
    id                 bigint auto_increment primary key,
    attached_photo_cnt int          not null,
    content_length     int          not null,
    created_at         datetime(6)  not null,
    user_current_point int          not null,
    description        varchar(255) not null,
    mileage_id         varchar(255) not null,
    place_id           varchar(255) not null,
    point              int          not null,
    review_id          varchar(255) not null,
    state              varchar(255) not null,
    user_id            varchar(255) not null
);

create index USER_ID_INDEX
    on mileage_history (user_id);

