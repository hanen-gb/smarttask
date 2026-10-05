package com.smarttask.backend.notification;

import com.smarttask.backend.notification.dto.NotificationResponse;
import com.smarttask.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getMyNotifications(@AuthenticationPrincipal User currentUser) {
        return notificationService.getMyNotifications(currentUser);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(@AuthenticationPrincipal User currentUser) {
        return Map.of("count", notificationService.getUnreadCount(currentUser));
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        notificationService.markAsRead(id, currentUser);
    }
}
