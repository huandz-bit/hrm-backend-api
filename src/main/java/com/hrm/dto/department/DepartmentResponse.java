package com.hrm.dto.department;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private Long id;
    private String name;
    private String description;
    private Long managerId;
    private String managerName;
    private int memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
