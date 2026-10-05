package com.smarttask.backend.comment;

import com.smarttask.backend.comment.dto.CommentRequest;
import com.smarttask.backend.comment.dto.CommentResponse;
import com.smarttask.backend.notification.event.CommentAddedEvent;
import com.smarttask.backend.project.ProjectMemberRepository;
import com.smarttask.backend.task.Task;
import com.smarttask.backend.task.TaskRepository;
import com.smarttask.backend.user.Role;
import com.smarttask.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    // Publie des evenements dans l'application -- ne sait PAS qui ecoute,
    // juste "voici ce qui s'est passe". NotificationService ecoute de son cote.
    private final ApplicationEventPublisher eventPublisher;

    public CommentResponse addComment(UUID taskId, User currentUser, CommentRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache introuvable"));

        requireMembership(task, currentUser);

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setTask(task);
        comment.setAuthor(currentUser);
        Comment saved = commentRepository.save(comment);

        eventPublisher.publishEvent(new CommentAddedEvent(saved));

        return toResponse(saved);
    }

    public List<CommentResponse> getTaskComments(UUID taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache introuvable"));
        requireMembership(task, currentUser);

        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteComment(UUID commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));

        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        if (!isAuthor && currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul l'auteur ou un admin peut supprimer ce commentaire");
        }

        commentRepository.delete(comment);
    }

    private void requireMembership(Task task, User currentUser) {
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(task.getProject().getId(), currentUser.getId());
        if (!isMember && currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'etes pas membre de ce projet");
        }
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId().toString(),
                comment.getContent(),
                new CommentResponse.AuthorRef(
                        comment.getAuthor().getId().toString(),
                        comment.getAuthor().getFirstName(),
                        comment.getAuthor().getLastName()
                ),
                comment.getCreatedAt()
        );
    }
}
