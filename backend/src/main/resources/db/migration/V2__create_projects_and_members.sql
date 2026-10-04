CREATE TABLE projects (
    id          UUID PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    description TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PLANNING',
    color       VARCHAR(7),
    start_date  DATE,
    end_date    DATE,
    owner_id    UUID         NOT NULL REFERENCES users(id),
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_projects_owner ON projects (owner_id);

CREATE TABLE project_members (
    id           UUID PRIMARY KEY,
    project_id   UUID         NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id      UUID         NOT NULL REFERENCES users(id),
    project_role VARCHAR(20)  NOT NULL DEFAULT 'CONTRIBUTOR',
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ  NOT NULL,

    CONSTRAINT uq_project_member UNIQUE (project_id, user_id)
);

CREATE INDEX idx_project_members_user ON project_members (user_id);
