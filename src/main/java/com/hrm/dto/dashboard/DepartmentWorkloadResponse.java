package com.hrm.dto.dashboard;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentWorkloadResponse {

    private Long departmentId;
    private String departmentName;
    private int totalEmployees;
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private List<EmployeeWorkload> employees;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeWorkload {
        private Long userId;
        private String fullName;
        private String email;
        private int totalTasks;
        private int completedTasks;
        private int inProgressTasks;
        private int overdueTasks;
    }
}
