package com.hrm.service;

import com.hrm.dto.comment.CommentRequest;
import com.hrm.dto.comment.CommentResponse;

import java.util.List;

public interface TaskCommentService {

    List<CommentResponse> getCommentsByTaskId(Long taskId);

    CommentResponse addComment(Long taskId, CommentRequest request, Long currentUserId);

    void deleteComment(Long commentId, Long currentUserId);
}
