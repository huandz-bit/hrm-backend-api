package com.hrm.service;

import com.hrm.dto.task.TaskAssignRequest;
import com.hrm.dto.task.TaskRequest;
import com.hrm.dto.task.TaskResponse;
import com.hrm.dto.task.TaskStatusUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

    Page<TaskResponse> getTasksByProject(Long projectId, String status, Pageable pageable);

    List<TaskResponse> getTasksByProjectList(Long projectId);

    Page<TaskResponse> getMyAssignedTasks(Long currentUserId, Pageable pageable);

    List<TaskResponse> getMyFollowedTasks(Long currentUserId);

    TaskResponse getById(Long id);

    TaskResponse create(TaskRequest request, Long currentUserId);

    TaskResponse update(Long id, TaskRequest request, Long currentUserId);

    TaskResponse updateStatus(Long id, TaskStatusUpdateRequest request, Long currentUserId);

    TaskResponse assignTask(Long id, TaskAssignRequest request, Long currentUserId);

    TaskResponse addFollower(Long taskId, Long userId, Long currentUserId);

    TaskResponse removeFollower(Long taskId, Long userId, Long currentUserId);

    TaskResponse addDependency(Long taskId, Long dependsOnTaskId, Long currentUserId);

    TaskResponse removeDependency(Long taskId, Long dependsOnTaskId, Long currentUserId);

    void delete(Long id, Long currentUserId);
}
