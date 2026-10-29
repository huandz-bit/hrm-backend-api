package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.PageResponse;
import com.hrm.dto.activity.ActivityLogResponse;
import com.hrm.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
@Validated
@Tag(name = "Activity Logs", description = "Audit trail and activity history for Task and Project")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    @Operation(summary = "Get activity logs for an entity (TASK or PROJECT)")
    public ResponseEntity<ApiResponse<?>> getLogs(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam(required = false, defaultValue = "false") boolean paginated,
            Pageable pageable) {
        if (paginated) {
            Page<ActivityLogResponse> page = activityLogService.getLogsPage(entityType, entityId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Activity logs retrieved successfully", new PageResponse<>(page)));
        }
        List<ActivityLogResponse> list = activityLogService.getLogs(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success("Activity logs retrieved successfully", list));
    }
}
