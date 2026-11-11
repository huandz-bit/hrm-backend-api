package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.hr.HrScorecardResponse;
import com.hrm.service.HrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/hr", "/hr"})
@RequiredArgsConstructor
@Validated
@Tag(name = "HR & Performance", description = "HR scorecard and employee evaluation APIs")
public class HrController {

    private final HrService hrService;

    @GetMapping("/scorecards")
    @Operation(summary = "Get HR performance scorecards", description = "Calculate KPIs, completion rates, and performance scores for all employees or by department")
    public ResponseEntity<ApiResponse<List<HrScorecardResponse>>> getScorecards(
            @RequestParam(required = false) Long departmentId) {
        List<HrScorecardResponse> scorecards = hrService.getScorecards(departmentId);
        return ResponseEntity.ok(ApiResponse.success("HR scorecards retrieved successfully", scorecards));
    }
}
