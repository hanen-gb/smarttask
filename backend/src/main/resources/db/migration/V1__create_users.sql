CREATE TABLE users (
    id            UUID PRIMARY KEY,
    email         VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    first_name    VARCHAR(60)  NOT NULL,
    last_name     VARCHAR(60)  NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'MEMBER',
    avatar_url    VARCHAR(255),
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_users_email ON users (email);
