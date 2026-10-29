package com.hrm.dto.project;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long leaderId;
    private String leaderName;
    private Long createdById;
    private String createdByName;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int memberCount;
    private int totalTasks;
    private int completedTasks;
    private double progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
