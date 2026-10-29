package com.hrm.dto.projectmember;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Project role is required")
    private String projectRole; // PROJECT_LEADER, PROJECT_MANAGER, PROJECT_MEMBER
}
