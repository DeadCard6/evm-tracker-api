-- Database initialization script for EVM Tracker
-- Tables: users, user_roles, refresh_tokens, projects (created by Hibernate ddl-auto=update)
-- Initial data: admin user

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Projects table will be created by Hibernate (JPA entity with @Entity @Table)
-- Projects table schema:
-- id BIGSERIAL PRIMARY KEY
-- name VARCHAR(255) NOT NULL
-- user_id BIGINT NOT NULL REFERENCES users(id)

-- Activities table will be created by Hibernate
-- Activities table schema:
-- id BIGSERIAL PRIMARY KEY
-- project_id BIGINT NOT NULL
-- name VARCHAR(255) NOT NULL
-- bac NUMERIC(19,4)
-- planned_percent_complete NUMERIC(19,4)
-- actual_percent_complete NUMERIC(19,4)
-- actual_cost NUMERIC(19,4)

-- Insert admin user (password: admin123)
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$7f6KpKBhdV/wcQ7JZv1L3Oa4J6T7Z4K9M2Bq0Xe5TqHh2LwN5M5JG') ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ADMIN') ON CONFLICT DO NOTHING;
