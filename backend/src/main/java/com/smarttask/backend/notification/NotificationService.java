package com.smarttask.backend.notification;

import com.smarttask.backend.notification.dto.NotificationResponse;
import com.smarttask.backend.notification.event.CommentAddedEvent;
import com.smarttask.backend.notification.event.TaskAssignedEvent;
import com.smarttask.backend.task.Task;
import com.smarttask.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ---- Ecouteurs d'evenements : declenches automatiquement par Spring ----

    @EventListener
    public void onTaskAssigned(TaskAssignedEvent event) {
        Task task = event.task();
        if (task.getAssignee() == null) return;

        // R17 : jamais se notifier soi-meme
        if (task.getAssignee().getId().equals(event.assignedBy().getId())) return;

        notify(task.getAssignee(), NotificationType.TASK_ASSIGNED,
                "Nouvelle tache assignee",
                "\"" + task.getTitle() + "\" vous a ete assignee",
                "/tasks/" + task.getId());
    }

    @EventListener
    public void onCommentAdded(CommentAddedEvent event) {
        Task task = event.comment().getTask();
        User author = event.comment().getAuthor();

        // Notifie l'assignee ET le rapporteur, sauf l'auteur du commentaire lui-meme
        notifyIfDifferent(task.getAssignee(), author, task, "a commente votre tache");
        notifyIfDifferent(task.getReporter(), author, task, "a commente une tache que vous avez creee");
    }

    private void notifyIfDifferent(User recipient, User author, Task task, String message) {
        if (recipient == null || recipient.getId().equals(author.getId())) return;

        notify(recipient, NotificationType.COMMENT_ADDED,
                "Nouveau commentaire",
                author.getFirstName() + " " + message + " \"" + task.getTitle() + "\"",
                "/tasks/" + task.getId());
    }

    private void notify(User recipient, NotificationType type, String title, String message, String link) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setLink(link);
        notificationRepository.save(notification);
    }

    // ---- Lecture (appele par NotificationController) ----

    public List<NotificationResponse> getMyNotifications(User currentUser) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public long getUnreadCount(User currentUser) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(currentUser.getId());
    }

    public void markAsRead(UUID notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification introuvable"));

        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette notification ne vous appartient pas");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId().toString(), n.getType(), n.getTitle(), n.getMessage(),
                n.getLink(), n.isRead(), n.getCreatedAt()
        );
    }
}
