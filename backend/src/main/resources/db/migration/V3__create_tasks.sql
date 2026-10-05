CREATE TABLE tasks (
    id            UUID PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    status        VARCHAR(20)  NOT NULL DEFAULT 'TODO',
    priority      VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    due_date      TIMESTAMPTZ,
    position      INT          NOT NULL DEFAULT 0,
    project_id    UUID         NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    assignee_id   UUID         REFERENCES users(id),
    reporter_id   UUID         NOT NULL REFERENCES users(id),
    completed_at  TIMESTAMPTZ,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_tasks_project ON tasks (project_id);
CREATE INDEX idx_tasks_assignee ON tasks (assignee_id);
CREATE INDEX idx_tasks_status ON tasks (status);
CREATE INDEX idx_tasks_due_date ON tasks (due_date);
