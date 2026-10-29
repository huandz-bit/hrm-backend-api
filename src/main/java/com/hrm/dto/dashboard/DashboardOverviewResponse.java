package com.hrm.dto.dashboard;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardOverviewResponse {

    private long totalProjects;
    private long activeProjects;
    private long completedProjects;

    private long totalTasks;
    private long completedTasks;
    private long inProgressTasks;
    private long reviewTasks;
    private long todoTasks;
    private long overdueTasks;

    private Map<String, Long> tasksByStatus;
    private Map<String, Long> tasksByPriority;
}
