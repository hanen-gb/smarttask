package com.smarttask.backend.task.dto;

import java.util.UUID;

// assigneeId nullable volontairement : permet de DE-assigner une tache (null)
public record UpdateTaskAssigneeRequest(UUID assigneeId) {}
