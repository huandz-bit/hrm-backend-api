package com.hrm.dto.task;

import com.hrm.dto.user.UserResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private Long projectId;
    private String projectName;
    private String title;
    private String description;
    private String priority;
    private String status;
    private Long assigneeId;
    private String assigneeName;
    private String assigneeEmail;
    private Long createdById;
    private String createdByName;
    private LocalDateTime deadline;
    private boolean isOverdue;
    private List<TaskSimpleResponse> dependencies;
    private List<UserResponse> followers;
    private int commentCount;
    private int attachmentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskSimpleResponse {
        private Long id;
        private String title;
        private String status;
        private String priority;
    }
}
