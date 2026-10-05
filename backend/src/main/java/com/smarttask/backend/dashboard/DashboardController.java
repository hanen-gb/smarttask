package com.smarttask.backend.dashboard;

import com.smarttask.backend.dashboard.dto.DashboardSummaryResponse;
import com.smarttask.backend.task.TaskPriority;
import com.smarttask.backend.task.TaskStatus;
import com.smarttask.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(@AuthenticationPrincipal User currentUser) {
        return dashboardService.getSummary(currentUser);
    }

    @GetMapping("/tasks-by-status")
    public Map<TaskStatus, Long> getTasksByStatus(@AuthenticationPrincipal User currentUser) {
        return dashboardService.getTasksByStatus(currentUser);
    }

    @GetMapping("/tasks-by-priority")
    public Map<TaskPriority, Long> getTasksByPriority(@AuthenticationPrincipal User currentUser) {
        return dashboardService.getTasksByPriority(currentUser);
    }
}
