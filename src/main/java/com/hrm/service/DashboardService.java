package com.hrm.service;

import com.hrm.dto.dashboard.DashboardOverviewResponse;
import com.hrm.dto.dashboard.DepartmentWorkloadResponse;
import com.hrm.dto.dashboard.HrDashboardOverviewResponse;

import java.util.List;

public interface DashboardService {

    DashboardOverviewResponse getOverview();

    List<DepartmentWorkloadResponse> getDepartmentWorkload();

    HrDashboardOverviewResponse getHrOverview();
}
