package com.hrm.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectPlanRequest {

    @NotBlank(message = "Project name or prompt is required")
    private String projectName;

    private String description;

    private Integer estimatedDurationDays;

    private List<String> targetDeliverables;

    private List<String> availableRoles;
}
