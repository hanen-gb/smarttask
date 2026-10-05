package com.smarttask.backend.attachment.dto;

import java.time.Instant;

public record AttachmentResponse(
        String id,
        String originalFilename,
        String contentType,
        long sizeBytes,
        String uploadedByName,
        Instant createdAt
) {}
