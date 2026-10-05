package com.smarttask.backend.notification.event;

import com.smarttask.backend.task.Task;
import com.smarttask.backend.user.User;

/**
 * Publie quand une tache change d'assignee. N'importe quelle partie de
 * l'application peut "ecouter" cet evenement sans que TaskService n'ait besoin
 * de connaitre NotificationService -- decouplage.
 */
public record TaskAssignedEvent(Task task, User assignedBy) {}
