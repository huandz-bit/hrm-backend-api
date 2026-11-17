package com.hrm.service;
import com.hrm.dto.profile.ProfileChangeRequestResponse; import com.hrm.dto.profile.EmployeeProfileRequest; import com.hrm.security.UserPrincipal; import java.util.List;
public interface ProfileChangeRequestService { ProfileChangeRequestResponse submit(EmployeeProfileRequest request, UserPrincipal currentUser); List<ProfileChangeRequestResponse> pending(UserPrincipal currentUser); List<ProfileChangeRequestResponse> all(UserPrincipal currentUser); ProfileChangeRequestResponse review(Long id, boolean approved, String note, UserPrincipal currentUser); }
