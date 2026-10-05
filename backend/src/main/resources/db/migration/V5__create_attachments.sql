CREATE TABLE attachments (
    id                 UUID PRIMARY KEY,
    original_filename  VARCHAR(255) NOT NULL,
    stored_filename    VARCHAR(255) NOT NULL UNIQUE,
    content_type       VARCHAR(100),
    size_bytes         BIGINT NOT NULL,
    task_id            UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    uploaded_by_id     UUID NOT NULL REFERENCES users(id),
    created_at         TIMESTAMPTZ NOT NULL,
    updated_at         TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_attachments_task ON attachments (task_id);
