
\connect MuscleDB

create table et_users(
user_id integer primary key not null,
first_name varchar(20) not null,
last_name varchar(20) not null,
email varchar(30) not null,
password text not null
);


create table et_categories(
category_id integer primary key not null,
user_id integer not null,
title varchar(20) not null,
description varchar(50) not null
);

alter table et_categories add constraint cat_users_fk foreign key (user_id) references et_users (user_id);

TRUNCATE TABLE et_users CASCADE;

CREATE SEQUENCE ET_USERS_SEQ;







