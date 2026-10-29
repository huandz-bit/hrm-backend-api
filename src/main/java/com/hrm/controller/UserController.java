package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.PageResponse;
import com.hrm.dto.user.UserRequest;
import com.hrm.dto.user.UserResponse;
import com.hrm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "User management APIs for Admin / HR")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users with pagination, optionally filter by department")
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) Long departmentId,
            Pageable pageable) {
        if (departmentId != null) {
            List<UserResponse> users = userService.getByDepartmentId(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        }
        Page<UserResponse> page = userService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", new PageResponse<>(page)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        UserResponse response = userService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @PostMapping
    @Operation(summary = "Create a new user / employee")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user information")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id,
                                                            @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate user")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
    }
}
