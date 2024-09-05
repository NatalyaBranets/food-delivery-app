create sequence ratings_seq start with 1 increment by 50;
create sequence role_id_seq start with 3 increment by 1;
create sequence usr_id_seq start with 2 increment by 1;

create table categories (
    id   bigserial    not null,
    name varchar(255) not null,
    primary key (id)
);

create table foods (
    id            bigserial      not null,
    name          varchar(255)   not null,
    description   varchar(2000),
    image         bytea,
    price         numeric(38, 2) not null,
    category_id   bigint         not null,
    restaurant_id bigint         not null,
    primary key (id)
);

create table order_items (
    id       bigserial      not null,
    quantity integer        not null,
    price    numeric(38, 2) not null,
    food_id  bigint         not null,
    order_id uuid           not null,
    primary key (id)
);

create table orders (
    id         uuid         not null,
    order_time timestamp(6) not null,
    is_paid    boolean,
    status     varchar(255) not null check (status in ('CANCELLED', 'PROCESSING', 'DELIVERED')),
    user_id    bigint       not null,
    primary key (id)
);

create table payments (
    id             bigserial      not null,
    total_amount   numeric(38, 2) not null,
    payment_date   timestamp(6)   not null,
    payment_status varchar(255)   not null check (payment_status in ('PAID', 'CANCELED', 'IN_PROGRESS', 'ERROR')),
    order_id       uuid           not null unique,
    primary key (id)
);

create table ratings (
    id            bigint       not null,
    description   varchar(255),
    rating        integer      not null,
    rating_date   timestamp(6) not null,
    user_id       bigint       not null,
    restaurant_id bigint       not null,
    order_id      uuid         not null unique,
    primary key (id)
);

create table restaurants (
    id      bigserial    not null,
    name    varchar(255) not null,
    address varchar(255) not null,
    phone   varchar(255) not null,
    primary key (id)
);

create table roles (
    id   bigint       not null,
    name varchar(255) not null check (name in ('USER', 'ADMIN')),
    primary key (id)
);

create table users (
    id         bigint       not null,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    email      varchar(255) not null unique,
    password   varchar(255) not null,
    phone      varchar(255) not null,
    address    varchar(255) not null,
    primary key (id)
);

create table users_roles (
    role_id bigint not null,
    user_id bigint not null,
    primary key (role_id, user_id)
);

alter table if exists foods
    add constraint fk_category foreign key (category_id) references categories;

alter table if exists foods
    add constraint fk_restaurant foreign key (restaurant_id) references restaurants;

alter table if exists order_items
    add constraint fk_food foreign key (food_id) references foods;

alter table if exists order_items
    add constraint fk_order foreign key (order_id) references orders;

alter table if exists orders
    add constraint fk_user foreign key (user_id) references users;

alter table if exists payments
    add constraint fk_order foreign key (order_id) references orders;

alter table if exists ratings
    add constraint fk_order foreign key (order_id) references orders;

alter table if exists ratings
    add constraint fk_restaurant foreign key (restaurant_id) references restaurants;

alter table if exists ratings
    add constraint fk_user foreign key (user_id) references users;

alter table if exists users_roles
    add constraint fk_role foreign key (role_id) references roles;

alter table if exists users_roles
    add constraint fk_user foreign key (user_id) references users;