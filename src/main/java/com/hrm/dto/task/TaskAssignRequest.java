package com.hrm.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAssignRequest {

    @NotNull(message = "Assignee user ID is required")
    private Long assigneeId;

    private String note;
}
