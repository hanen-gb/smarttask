CREATE TABLE comments (
    id         UUID PRIMARY KEY,
    content    TEXT NOT NULL,
    task_id    UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    author_id  UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_comments_task ON comments (task_id);

CREATE TABLE notifications (
    id           UUID PRIMARY KEY,
    recipient_id UUID NOT NULL REFERENCES users(id),
    type         VARCHAR(30) NOT NULL,
    title        VARCHAR(150) NOT NULL,
    message      TEXT,
    link         VARCHAR(255),
    is_read      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_notifications_recipient_read ON notifications (recipient_id, is_read);
