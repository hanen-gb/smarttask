package com.smarttask.backend.comment.dto;

import java.time.Instant;

public record CommentResponse(
        String id,
        String content,
        AuthorRef author,
        Instant createdAt
) {
    public record AuthorRef(String id, String firstName, String lastName) {}
}
