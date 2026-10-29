package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.dashboard.DashboardOverviewResponse;
import com.hrm.dto.dashboard.DepartmentWorkloadResponse;
import com.hrm.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Validated
@Tag(name = "Dashboard & Reports", description = "Dashboard analytics, task statistics, and department workload APIs")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    @Operation(summary = "Get overall dashboard statistics (tasks by status, overdue count, active projects)")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getOverview() {
        DashboardOverviewResponse response = dashboardService.getOverview();
        return ResponseEntity.ok(ApiResponse.success("Dashboard overview retrieved successfully", response));
    }

    @GetMapping("/department-workload")
    @Operation(summary = "Get workload report by department and employees")
    public ResponseEntity<ApiResponse<List<DepartmentWorkloadResponse>>> getDepartmentWorkload() {
        List<DepartmentWorkloadResponse> list = dashboardService.getDepartmentWorkload();
        return ResponseEntity.ok(ApiResponse.success("Department workload retrieved successfully", list));
    }
}
