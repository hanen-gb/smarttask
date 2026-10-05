package com.smarttask.backend.task;

import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// Chaque methode renvoie une Specification<Task> = "un petit bout de clause WHERE".
// On les combine ensuite avec .and() dans TaskService -- aucune ne connait les
// autres, elles se combinent a la demande selon les parametres recus.
public final class TaskSpecifications {

    private TaskSpecifications() {}

    public static Specification<Task> projectIdEquals(UUID projectId) {
        return (root, query, cb) -> cb.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> inProjects(List<UUID> projectIds) {
        return (root, query, cb) -> root.get("project").get("id").in(projectIds);
    }

    public static Specification<Task> assigneeIdEquals(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("assignee").get("id"), userId);
    }

    public static Specification<Task> unassigned() {
        return (root, query, cb) -> cb.isNull(root.get("assignee"));
    }

    public static Specification<Task> statusIn(List<TaskStatus> statuses) {
        return (root, query, cb) -> root.get("status").in(statuses);
    }

    public static Specification<Task> priorityIn(List<TaskPriority> priorities) {
        return (root, query, cb) -> root.get("priority").in(priorities);
    }

    public static Specification<Task> titleContains(String text) {
        String pattern = "%" + text.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern);
    }

    public static Specification<Task> dueBefore(Instant date) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("dueDate"), date);
    }
}
