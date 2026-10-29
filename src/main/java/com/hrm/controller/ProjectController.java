package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.PageResponse;
import com.hrm.dto.project.ProjectRequest;
import com.hrm.dto.project.ProjectResponse;
import com.hrm.dto.projectmember.ProjectMemberRequest;
import com.hrm.dto.projectmember.ProjectMemberResponse;
import com.hrm.security.UserPrincipal;
import com.hrm.service.ProjectService;
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
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Validated
@Tag(name = "Project Management", description = "Project and Project Member management APIs")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "Get all projects or user's joined projects")
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false, defaultValue = "false") boolean myProjects,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        if (myProjects && userPrincipal != null) {
            List<ProjectResponse> myProjectsList = projectService.getMyProjects(userPrincipal.getId());
            return ResponseEntity.ok(ApiResponse.success("User projects retrieved successfully", myProjectsList));
        }
        Page<ProjectResponse> page = projectService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", new PageResponse<>(page)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ApiResponse<ProjectResponse>> getById(@PathVariable Long id) {
        ProjectResponse response = projectService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Project retrieved successfully", response));
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ApiResponse<ProjectResponse>> create(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : (request.getLeaderId() != null ? request.getLeaderId() : 1L);
        ProjectResponse response = projectService.create(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project")
    public ResponseEntity<ApiResponse<ProjectResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        ProjectResponse response = projectService.update(id, request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", response));
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Close a project (Project Leader)")
    public ResponseEntity<ApiResponse<ProjectResponse>> closeProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        ProjectResponse response = projectService.closeProject(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Project closed successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a project")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        projectService.delete(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Project cancelled successfully", null));
    }

    // --- Project Member APIs ---

    @GetMapping("/{id}/members")
    @Operation(summary = "Get all members of a project")
    public ResponseEntity<ApiResponse<List<ProjectMemberResponse>>> getMembers(@PathVariable Long id) {
        List<ProjectMemberResponse> members = projectService.getMembers(id);
        return ResponseEntity.ok(ApiResponse.success("Project members retrieved successfully", members));
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Add a member to a project")
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> addMember(
            @PathVariable Long id,
            @Valid @RequestBody ProjectMemberRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        ProjectMemberResponse response = projectService.addMember(id, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project member added successfully", response));
    }

    @PutMapping("/{id}/members/{userId}")
    @Operation(summary = "Update member role in project")
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> updateMemberRole(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestParam String projectRole,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        ProjectMemberResponse response = projectService.updateMemberRole(id, userId, projectRole, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Project member role updated successfully", response));
    }

    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "Remove a member from project")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        projectService.removeMember(id, userId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Project member removed successfully", null));
    }
}
