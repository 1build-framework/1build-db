create schema if not exists auth;
CREATE TABLE if not exists auth.applications (
  id BIGSERIAL PRIMARY KEY,
  application_code VARCHAR(255) NOT NULL UNIQUE,
  application_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS auth.tenants (
  id BIGSERIAL PRIMARY KEY,
  tenant_code VARCHAR(255) NOT NULL UNIQUE,
  tenant_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS auth.tenant_application_mapping (
  id BIGSERIAL PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  application_id BIGINT NOT NULL,
  CONSTRAINT tenant_application_unique UNIQUE (tenant_id, application_id),
  FOREIGN KEY (tenant_id) REFERENCES auth.tenants(id),
  FOREIGN KEY (application_id) REFERENCES auth.applications(id)
);
