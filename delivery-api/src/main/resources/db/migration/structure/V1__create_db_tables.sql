create sequence ratings_seq start with 1 increment by 50;
create sequence role_id_seq start with 3 increment by 1;
create sequence usr_id_seq start with 2 increment by 1;

create table foods
(
    id            bigserial      not null,
    category      varchar(255)   not null check (category in
                                                 ('APPETIZER', 'DESSERT', 'DRINK', 'MAIN_COURSE', 'SOUP', 'SALAD')),
    description   varchar(255),
    image         bytea,
    name          varchar(255)   not null,
    price         numeric(38, 2) not null,
    restaurant_id bigint         not null,
    primary key (id)
);

create table order_items
(
    id       bigserial      not null,
    price    numeric(38, 2) not null,
    quantity integer        not null,
    food_id  bigint         not null,
    order_id uuid           not null,
    primary key (id)
);

create table orders
(
    id         uuid         not null,
    is_paid    boolean,
    order_time timestamp(6) not null,
    status     varchar(255) not null check (status in ('CANCELLED', 'PROCESSING', 'DELIVERED')),
    user_id    bigint       not null,
    primary key (id)
);

create table ratings
(
    id            bigint       not null,
    description   varchar(255),
    rating        integer      not null check ((rating >= 1) and (rating <= 10)),
    rating_date   timestamp(6) not null,
    restaurant_id bigint       not null,
    user_id       bigint       not null,
    primary key (id)
);

create table restaurants
(
    id      bigserial    not null,
    address varchar(255) not null,
    name    varchar(255) not null,
    phone   varchar(255) not null,
    primary key (id),
    unique (name, address)
);

create table roles
(
    id   bigint       not null,
    name varchar(255) not null check (name in ('USER', 'ADMIN')),
    primary key (id)
);

create table users
(
    id         bigint       not null,
    address    varchar(255) not null,
    email      varchar(255) not null unique,
    first_name varchar(30)  not null,
    is_active  boolean,
    is_enabled  boolean,
    last_name  varchar(30)  not null,
    password   varchar(255) not null,
    phone      varchar(255) not null,
    verification_code varchar(64),
    primary key (id)
);

create table users_roles
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id)
);

alter table if exists foods
    add constraint fk_restaurant foreign key (restaurant_id) references restaurants;

alter table if exists order_items
    add constraint fk_food foreign key (food_id) references foods;

alter table if exists order_items
    add constraint fk_order foreign key (order_id) references orders;

alter table if exists orders
    add constraint fk_user foreign key (user_id) references users;

alter table if exists ratings
    add constraint fk_restaurant foreign key (restaurant_id) references restaurants;

alter table if exists ratings
    add constraint fk_user foreign key (user_id) references users;

alter table if exists users_roles
    add constraint fk_role foreign key (role_id) references roles;

alter table if exists users_roles
    add constraint fk_user foreign key (user_id) references users;