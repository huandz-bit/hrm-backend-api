package com.hrm.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private String priority; // LOW, MEDIUM, HIGH, URGENT

    private String status; // TODO, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED

    private Long assigneeId;

    private LocalDateTime deadline;

    private Set<Long> dependencyTaskIds;

    private Set<Long> followerUserIds;
}
