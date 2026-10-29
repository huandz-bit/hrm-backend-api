package com.hrm.service.impl;

import com.hrm.dto.comment.CommentRequest;
import com.hrm.dto.comment.CommentResponse;
import com.hrm.entity.ActivityLog;
import com.hrm.entity.Task;
import com.hrm.entity.TaskComment;
import com.hrm.entity.User;
import com.hrm.exception.ResourceNotFoundException;
import com.hrm.repository.ActivityLogRepository;
import com.hrm.repository.TaskCommentRepository;
import com.hrm.repository.TaskRepository;
import com.hrm.repository.UserRepository;
import com.hrm.service.TaskCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTaskId(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public CommentResponse addComment(Long taskId, CommentRequest request, Long currentUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        TaskComment comment = TaskComment.builder()
                .task(task)
                .user(user)
                .content(request.getContent())
                .build();

        comment = commentRepository.save(comment);

        // Activity log
        ActivityLog log = ActivityLog.builder()
                .entityType("TASK")
                .entityId(taskId)
                .action("COMMENT_ADDED")
                .newValue(user.getFullName() + " added a comment")
                .performedBy(user)
                .build();
        activityLogRepository.save(log);

        return mapToResponse(comment);
    }

    @Override
    public void deleteComment(Long commentId, Long currentUserId) {
        TaskComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(TaskComment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTask().getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getFullName())
                .userEmail(comment.getUser().getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
