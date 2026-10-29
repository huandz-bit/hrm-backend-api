package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.PageResponse;
import com.hrm.dto.task.TaskAssignRequest;
import com.hrm.dto.task.TaskRequest;
import com.hrm.dto.task.TaskResponse;
import com.hrm.dto.task.TaskStatusUpdateRequest;
import com.hrm.security.UserPrincipal;
import com.hrm.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Validated
@Tag(name = "Task Management", description = "Core Task APIs (CRUD, assignment, status update, dependencies, followers)")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Get tasks by project ID with pagination, optionally filter by status")
    public ResponseEntity<ApiResponse<?>> getTasksByProject(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "false") boolean listOnly,
            Pageable pageable) {
        if (projectId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("projectId parameter is required"));
        }
        if (listOnly) {
            List<TaskResponse> list = taskService.getTasksByProjectList(projectId);
            return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", list));
        }
        Page<TaskResponse> page = taskService.getTasksByProject(projectId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", new PageResponse<>(page)));
    }

    @GetMapping("/my-tasks")
    @Operation(summary = "Get current logged-in user's assigned tasks")
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> getMyTasks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        Page<TaskResponse> page = taskService.getMyAssignedTasks(currentUserId, pageable);
        return ResponseEntity.ok(ApiResponse.success("User assigned tasks retrieved successfully", new PageResponse<>(page)));
    }

    @GetMapping("/my-following")
    @Operation(summary = "Get tasks current user is following")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getMyFollowedTasks(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        List<TaskResponse> list = taskService.getMyFollowedTasks(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Followed tasks retrieved successfully", list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID (with followers and dependencies)")
    public ResponseEntity<ApiResponse<TaskResponse>> getById(@PathVariable Long id) {
        TaskResponse response = taskService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Task retrieved successfully", response));
    }

    @PostMapping
    @Operation(summary = "Create a new task in project")
    public ResponseEntity<ApiResponse<TaskResponse>> create(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        TaskResponse response = taskService.create(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task details")
    public ResponseEntity<ApiResponse<TaskResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        TaskResponse response = taskService.update(id, request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status (TODO, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED)")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        TaskResponse response = taskService.updateStatus(id, request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Task status updated successfully", response));
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assign or transfer task to another user")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskAssignRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        TaskResponse response = taskService.assignTask(id, request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Task assigned successfully", response));
    }

    @PostMapping("/{id}/followers/{userId}")
    @Operation(summary = "Add a follower to task")
    public ResponseEntity<ApiResponse<TaskResponse>> addFollower(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        TaskResponse response = taskService.addFollower(id, userId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Follower added successfully", response));
    }

    @DeleteMapping("/{id}/followers/{userId}")
    @Operation(summary = "Remove a follower from task")
    public ResponseEntity<ApiResponse<TaskResponse>> removeFollower(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        TaskResponse response = taskService.removeFollower(id, userId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Follower removed successfully", response));
    }

    @PostMapping("/{id}/dependencies/{dependsOnTaskId}")
    @Operation(summary = "Add dependency (task that must be completed first)")
    public ResponseEntity<ApiResponse<TaskResponse>> addDependency(
            @PathVariable Long id,
            @PathVariable Long dependsOnTaskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        TaskResponse response = taskService.addDependency(id, dependsOnTaskId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Dependency added successfully", response));
    }

    @DeleteMapping("/{id}/dependencies/{dependsOnTaskId}")
    @Operation(summary = "Remove task dependency")
    public ResponseEntity<ApiResponse<TaskResponse>> removeDependency(
            @PathVariable Long id,
            @PathVariable Long dependsOnTaskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        TaskResponse response = taskService.removeDependency(id, dependsOnTaskId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Dependency removed successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        taskService.delete(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
}
