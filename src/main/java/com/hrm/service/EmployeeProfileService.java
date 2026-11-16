package com.hrm.service;

import com.hrm.dto.profile.EmployeeProfileRequest;
import com.hrm.dto.profile.EmployeeProfileResponse;
import com.hrm.security.UserPrincipal;

public interface EmployeeProfileService {

    EmployeeProfileResponse getProfile(Long targetUserId, UserPrincipal currentUser);

    EmployeeProfileResponse updateProfile(Long targetUserId, EmployeeProfileRequest request, UserPrincipal currentUser);
}
