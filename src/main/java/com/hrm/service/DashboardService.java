package com.hrm.service;

import com.hrm.dto.dashboard.DashboardOverviewResponse;
import com.hrm.dto.dashboard.DepartmentWorkloadResponse;

import java.util.List;

public interface DashboardService {

    DashboardOverviewResponse getOverview();

    List<DepartmentWorkloadResponse> getDepartmentWorkload();
}
