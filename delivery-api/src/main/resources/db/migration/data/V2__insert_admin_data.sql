insert into roles(id, name) values (1, 'USER');
insert into roles(id, name) values (2, 'ADMIN');

insert into users(id, first_name, last_name, email, password, phone, address, is_active)
values (1, 'admin', 'admin', 'admin@gmail.com', '$2a$12$PiTNGzcKN5B7c7e6y1dleuf6PBeLE3U/mtFRRt.3wQDFucoBbKd46', '+38077777777', 'Lviv', true);

insert into users_roles (role_id, user_id) values (2, 1);