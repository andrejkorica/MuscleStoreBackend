
\connect MuscleDB


create table et_users(
user_id integer primary key not null,
first_name varchar(20) not null,
last_name varchar(20) not null,
email varchar(30) not null,
password text not null
);

-- Create workout_plans table
CREATE TABLE workout_plans (
    plan_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES et_users(user_id) ON DELETE CASCADE,
    title VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create plan_sections table
CREATE TABLE plan_sections (
    section_id SERIAL PRIMARY KEY,
    plan_id INTEGER NOT NULL REFERENCES workout_plans(plan_id) ON DELETE CASCADE,
    title VARCHAR(50) NOT NULL
);

-- Create exercises table
CREATE TABLE exercises (
    exercise_id SERIAL PRIMARY KEY,
    section_id INTEGER NOT NULL REFERENCES plan_sections(section_id) ON DELETE CASCADE,
    title VARCHAR(50) NOT NULL,
    reps VARCHAR(50) NOT NULL
);

TRUNCATE TABLE et_users CASCADE;

TRUNCATE TABLE workout_plans, plan_sections, exercises CASCADE;

DROP TABLE IF EXISTS exercises;
DROP TABLE IF EXISTS plan_sections;
DROP TABLE IF EXISTS workout_plans;







