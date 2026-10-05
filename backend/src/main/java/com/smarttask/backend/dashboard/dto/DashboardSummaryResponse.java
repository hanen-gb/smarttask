package com.smarttask.backend.dashboard.dto;

public record DashboardSummaryResponse(
        long totalProjects,
        long activeProjects,
        long totalTasks,
        long myOpenTasks,
        long myOverdueTasks,
        long tasksCompletedThisWeek,
        double completionRate
) {}
