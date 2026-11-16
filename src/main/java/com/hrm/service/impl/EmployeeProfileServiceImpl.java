package com.hrm.service.impl;

import com.hrm.dto.profile.EmployeeProfileDto;
import com.hrm.dto.profile.EmployeeProfileRequest;
import com.hrm.dto.profile.EmployeeProfileResponse;
import com.hrm.entity.EmployeeProfile;
import com.hrm.entity.User;
import com.hrm.exception.BadRequestException;
import com.hrm.exception.ResourceNotFoundException;
import com.hrm.repository.EmployeeProfileRepository;
import com.hrm.repository.UserRepository;
import com.hrm.security.UserPrincipal;
import com.hrm.service.EmployeeProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeProfileServiceImpl implements EmployeeProfileService {

    private final UserRepository userRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public EmployeeProfileResponse getProfile(Long targetUserId, UserPrincipal currentUser) {
        validateAccessPermission(targetUserId, currentUser);

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));

        EmployeeProfile profile = employeeProfileRepository.findById(targetUserId).orElse(null);

        return mapToResponse(user, profile);
    }

    @Override
    public EmployeeProfileResponse updateProfile(Long targetUserId, EmployeeProfileRequest request, UserPrincipal currentUser) {
        validateAccessPermission(targetUserId, currentUser);

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));

        EmployeeProfile profile = employeeProfileRepository.findById(targetUserId).orElse(null);

        if (profile == null) {
            profile = EmployeeProfile.builder()
                    .user(user)
                    .avatarUrl(request.getAvatarUrl())
                    .phone(request.getPhone())
                    .dateOfBirth(request.getDateOfBirth())
                    .gender(request.getGender())
                    .address(request.getAddress())
                    .cccd(request.getCccd())
                    .skills(request.getSkills() != null ? request.getSkills() : new ArrayList<>())
                    .educations(request.getEducations() != null ? request.getEducations() : new ArrayList<>())
                    .certifications(request.getCertifications() != null ? request.getCertifications() : new ArrayList<>())
                    .languages(request.getLanguages() != null ? request.getLanguages() : new ArrayList<>())
                    .notes(request.getNotes() != null ? request.getNotes() : new HashMap<>())
                    .build();
        } else {
            profile.setAvatarUrl(request.getAvatarUrl());
            profile.setPhone(request.getPhone());
            profile.setDateOfBirth(request.getDateOfBirth());
            profile.setGender(request.getGender());
            profile.setAddress(request.getAddress());
            profile.setCccd(request.getCccd());
            profile.setSkills(request.getSkills() != null ? request.getSkills() : new ArrayList<>());
            profile.setEducations(request.getEducations() != null ? request.getEducations() : new ArrayList<>());
            profile.setCertifications(request.getCertifications() != null ? request.getCertifications() : new ArrayList<>());
            profile.setLanguages(request.getLanguages() != null ? request.getLanguages() : new ArrayList<>());
            profile.setNotes(request.getNotes() != null ? request.getNotes() : new HashMap<>());
        }

        profile = employeeProfileRepository.save(profile);
        return mapToResponse(user, profile);
    }

    /**
     * Quy tắc phân quyền:
     * - Người dùng tự xem/sửa hồ sơ của bản thân (targetUserId == currentUserId).
     * - Chỉ người có vai trò ADMIN mới được phép xem/sửa hồ sơ của người khác.
     */
    private void validateAccessPermission(Long targetUserId, UserPrincipal currentUser) {
        if (currentUser == null) {
            throw new BadRequestException("Unauthorized access");
        }

        boolean isSelf = targetUserId.equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getSystemRole());

        if (!isSelf && !isAdmin) {
            throw new BadRequestException("You do not have permission to view or modify this employee profile.");
        }
    }

    private EmployeeProfileResponse mapToResponse(User user, EmployeeProfile profile) {
        EmployeeProfileDto profileDto = null;
        if (profile != null) {
            profileDto = EmployeeProfileDto.builder()
                    .avatarUrl(profile.getAvatarUrl())
                    .phone(profile.getPhone())
                    .dateOfBirth(profile.getDateOfBirth())
                    .gender(profile.getGender())
                    .address(profile.getAddress())
                    .cccd(profile.getCccd())
                    .skills(profile.getSkills() != null ? profile.getSkills() : new ArrayList<>())
                    .educations(profile.getEducations() != null ? profile.getEducations() : new ArrayList<>())
                    .certifications(profile.getCertifications() != null ? profile.getCertifications() : new ArrayList<>())
                    .languages(profile.getLanguages() != null ? profile.getLanguages() : new ArrayList<>())
                    .notes(profile.getNotes() != null ? profile.getNotes() : new HashMap<>())
                    .createdAt(profile.getCreatedAt())
                    .updatedAt(profile.getUpdatedAt())
                    .build();
        }

        return EmployeeProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .systemRole(user.getSystemRole())
                .status(user.getStatus())
                .userCreatedAt(user.getCreatedAt())
                .userUpdatedAt(user.getUpdatedAt())
                .profile(profileDto)
                .build();
    }
}
