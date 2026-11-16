package com.hrm.dto.profile;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private Long departmentId;
    private String departmentName;
    private String systemRole;
    private String status;
    private LocalDateTime userCreatedAt;
    private LocalDateTime userUpdatedAt;

    private EmployeeProfileDto profile;
}
