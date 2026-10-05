package com.smarttask.backend.comment;

import com.smarttask.backend.comment.dto.CommentRequest;
import com.smarttask.backend.comment.dto.CommentResponse;
import com.smarttask.backend.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/tasks/{taskId}/comments")
    public List<CommentResponse> getTaskComments(@PathVariable UUID taskId, @AuthenticationPrincipal User currentUser) {
        return commentService.getTaskComments(taskId, currentUser);
    }

    @PostMapping("/api/tasks/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(
            @PathVariable UUID taskId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return commentService.addComment(taskId, currentUser, request);
    }

    @DeleteMapping("/api/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        commentService.deleteComment(id, currentUser);
    }
}
