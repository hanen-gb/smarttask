package com.smarttask.backend.notification.dto;

import com.smarttask.backend.notification.NotificationType;

import java.time.Instant;

public record NotificationResponse(
        String id,
        NotificationType type,
        String title,
        String message,
        String link,
        boolean isRead,
        Instant createdAt
) {}
