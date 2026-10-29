package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.comment.CommentRequest;
import com.hrm.dto.comment.CommentResponse;
import com.hrm.security.UserPrincipal;
import com.hrm.service.TaskCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Task Comments", description = "Task Comments and Discussion APIs")
public class TaskCommentController {

    private final TaskCommentService commentService;

    @GetMapping
    @Operation(summary = "Get all comments for a task")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long taskId) {
        List<CommentResponse> list = commentService.getCommentsByTaskId(taskId);
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", list));
    }

    @PostMapping
    @Operation(summary = "Add a comment to task")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        CommentResponse response = commentService.addComment(taskId, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment added successfully", response));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        commentService.deleteComment(commentId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }
}
