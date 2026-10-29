package com.hrm.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private Long departmentId;
    private String departmentName;
    private String systemRole;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
