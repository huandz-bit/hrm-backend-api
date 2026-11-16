package com.hrm.dto.profile;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProfileDto {

    private String avatarUrl;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String cccd;
    private List<Object> skills;
    private List<Object> educations;
    private List<Object> certifications;
    private List<Object> languages;
    private Map<String, Object> notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
