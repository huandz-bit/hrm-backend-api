package com.hrm.dto.task;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status; // TODO, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED

    private String note;
}
