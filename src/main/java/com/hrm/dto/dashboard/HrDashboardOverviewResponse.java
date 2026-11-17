package com.hrm.dto.dashboard;

import com.hrm.dto.department.DepartmentResponse;
import com.hrm.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * One payload for the People overview screen.  Keeping the two collections in
 * a single response gives the UI a consistent snapshot and avoids a partially
 * loaded dashboard when either of the legacy list endpoints fails.
 */
@Getter
@Builder
@AllArgsConstructor
public class HrDashboardOverviewResponse {
    private final List<UserResponse> users;
    private final List<DepartmentResponse> departments;
}
