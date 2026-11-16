package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.profile.EmployeeProfileRequest;
import com.hrm.dto.profile.EmployeeProfileResponse;
import com.hrm.security.UserPrincipal;
import com.hrm.service.EmployeeProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/employee-profiles", "/employee-profiles"})
@RequiredArgsConstructor
@Validated
@Tag(name = "Employee Profile", description = "Employee profile management APIs with JSONB support")
public class EmployeeProfileController {

    private final EmployeeProfileService employeeProfileService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user's profile", description = "Returns user details along with profile (or null if not yet created)")
    public ResponseEntity<ApiResponse<EmployeeProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        EmployeeProfileResponse response = employeeProfileService.getProfile(currentUserId, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @PutMapping("/me")
    @Operation(summary = "Update or create current authenticated user's profile", description = "Upserts employee profile without modifying user credentials or account data")
    public ResponseEntity<ApiResponse<EmployeeProfileResponse>> updateMyProfile(
            @Valid @RequestBody EmployeeProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        EmployeeProfileResponse response = employeeProfileService.updateProfile(currentUserId, request, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get employee profile by userId (Admin only)", description = "Admins can view any user's profile, regular users can only view their own")
    public ResponseEntity<ApiResponse<EmployeeProfileResponse>> getProfileByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        EmployeeProfileResponse response = employeeProfileService.getProfile(userId, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update or create employee profile by userId (Admin only)", description = "Admins can update any user's profile, regular users can only update their own")
    public ResponseEntity<ApiResponse<EmployeeProfileResponse>> updateProfileByUserId(
            @PathVariable Long userId,
            @Valid @RequestBody EmployeeProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        EmployeeProfileResponse response = employeeProfileService.updateProfile(userId, request, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }
}
