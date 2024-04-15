
\connect MuscleDB

create table et_users(
user_id integer primary key not null,
first_name varchar(20) not null,
last_name varchar(20) not null,
email varchar(30) not null,
password text not null
);

CREATE SEQUENCE ET_USERS_SEQ;







