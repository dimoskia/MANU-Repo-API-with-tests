create table record
(
    id                      bigserial primary key,
    title                   varchar(256) not null,
    authors                 varchar      not null,
    collection              smallint     not null default 17,
    department              smallint     not null,
    subject                 varchar(64)  not null,
    description_or_abstract varchar      not null,
    keywords                varchar(128),
    language                varchar(64),
    num_pages               integer,
    publication_date        date,
    publication_status      smallint,
    downloads_count         integer      not null default 0,
    date_archived           timestamp    not null default current_timestamp,
    approved                boolean      not null default false,
    private_record          boolean      not null default false,
    constraint valid_collection check (collection between 0 and 17),
    constraint valid_department check (department between 0 and 5),
    constraint valid_publication_status check (publication_status between 0 and 2),
    constraint publication_status_date
        check ((publication_status is not null and publication_status = 0) or publication_date is null)
);

create table file_data
(
    id   bigserial primary key,
    data bytea not null
);

create table file
(
    id           bigint primary key references file_data (id) on delete cascade,
    content_type varchar not null,
    file_name    varchar not null,
    size         integer not null,
    record_id    bigint  not null references record (id) on delete cascade
);

create table profile_image
(
    id           bigserial primary key,
    content_type varchar not null,
    data         bytea   not null
);

create table account
(
    id               bigserial primary key,
    email            varchar(254) unique not null,
    password         varchar             not null,
    role             varchar             not null default 'ROLE_USER',
    enabled          boolean             not null default false,
    first_name       varchar(32)         not null,
    last_name        varchar(32)         not null,
    academic_degree  smallint,
    academic_rank    smallint,
    member_type      smallint            not null,
    department       smallint            not null,
    short_bio        varchar,
    phone_number     varchar,
    workplace        varchar,
    profile_image_id bigint              references profile_image (id) on delete set null,
    constraint role_valid check (role in ('ROLE_ADMIN', 'ROLE_USER')),
    constraint academic_degree_valid check (academic_degree between 0 and 15),
    constraint academic_rank_valid check (academic_rank between 0 and 10),
    constraint member_type_valid check (member_type between 0 and 3),
    constraint department_valid check (department between 0 and 5)
);

create table record_account
(
    record_id  bigint references record (id) on delete cascade,
    account_id bigint references account (id) on delete cascade,
    primary key (record_id, account_id)
);