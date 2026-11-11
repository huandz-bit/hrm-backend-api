package com.hrm.dto.ai;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectPlanResponse {

    private String projectName;
    private String executiveSummary;
    private int estimatedTotalDays;
    private List<PlanPhase> phases;
    private List<SuggestedTask> suggestedTasks;
    private List<String> recommendedRoles;
    private List<String> potentialRisks;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanPhase {
        private String phaseName;
        private int durationDays;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SuggestedTask {
        private String title;
        private String description;
        private String priority; // LOW, MEDIUM, HIGH, URGENT
        private int estimatedDays;
        private String suggestedRole;
        private List<String> dependencyTaskTitles;
    }
}
