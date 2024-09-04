create schema if not exists auth;
CREATE TABLE if not exists auth.applications (
  id BIGSERIAL PRIMARY KEY,
  application_code VARCHAR(255) NOT NULL UNIQUE,
  application_name VARCHAR(255) NOT NULL
);