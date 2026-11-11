package com.hrm.dto.hr;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrScorecardResponse {

    private Long userId;
    private String fullName;
    private String email;
    private Long departmentId;
    private String departmentName;
    private String systemRole;

    private int totalAssignedTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int overdueTasks;
    private double completionRate; // percentage (0.0 - 100.0)
    private double performanceScore; // 0 - 100 scale based on timely completions & rate

    private List<String> skillsOrBadges;
}
