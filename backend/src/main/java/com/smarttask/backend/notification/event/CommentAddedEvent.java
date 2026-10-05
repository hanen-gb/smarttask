package com.smarttask.backend.notification.event;

import com.smarttask.backend.comment.Comment;

public record CommentAddedEvent(Comment comment) {}
