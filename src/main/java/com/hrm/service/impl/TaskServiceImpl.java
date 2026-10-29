package com.hrm.service.impl;

import com.hrm.dto.task.TaskAssignRequest;
import com.hrm.dto.task.TaskRequest;
import com.hrm.dto.task.TaskResponse;
import com.hrm.dto.task.TaskStatusUpdateRequest;
import com.hrm.dto.user.UserResponse;
import com.hrm.entity.*;
import com.hrm.exception.BadRequestException;
import com.hrm.exception.ResourceNotFoundException;
import com.hrm.repository.*;
import com.hrm.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByProject(Long projectId, String status, Pageable pageable) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        return taskRepository.findByProjectId(projectId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectList(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getMyAssignedTasks(Long currentUserId, Pageable pageable) {
        return taskRepository.findByAssigneeId(currentUserId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyFollowedTasks(Long currentUserId) {
        return taskRepository.findByFollowerUserId(currentUserId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return mapToResponse(task);
    }

    @Override
    public TaskResponse create(TaskRequest request, Long currentUserId) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));

        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        // Assignee handling
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee user not found with id: " + request.getAssigneeId()));
            validateTaskAssignment(project.getId(), creator, assignee);
        }

        // Dependencies
        Set<Task> dependencies = new HashSet<>();
        if (request.getDependencyTaskIds() != null && !request.getDependencyTaskIds().isEmpty()) {
            List<Task> depTasks = taskRepository.findAllById(request.getDependencyTaskIds());
            dependencies.addAll(depTasks);
        }

        // Followers
        Set<User> followers = new HashSet<>();
        if (request.getFollowerUserIds() != null && !request.getFollowerUserIds().isEmpty()) {
            List<User> followerUsers = userRepository.findAllById(request.getFollowerUserIds());
            followers.addAll(followerUsers);
        }

        Task task = Task.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .status(request.getStatus() != null ? request.getStatus() : "TODO")
                .assignee(assignee)
                .createdBy(creator)
                .deadline(request.getDeadline())
                .dependencies(dependencies)
                .followers(followers)
                .build();

        task = taskRepository.save(task);

        // Activity log
        logActivity(task.getId(), "CREATED", null, "Task created with status " + task.getStatus(), creator);

        return mapToResponse(task);
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request, Long currentUserId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDeadline() != null) {
            String oldDeadline = task.getDeadline() != null ? task.getDeadline().toString() : "None";
            task.setDeadline(request.getDeadline());
            logActivity(task.getId(), "DEADLINE_CHANGED", oldDeadline, request.getDeadline().toString(), currentUser);
        }

        if (request.getAssigneeId() != null && (task.getAssignee() == null || !task.getAssignee().getId().equals(request.getAssigneeId()))) {
            User newAssignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee user not found with id: " + request.getAssigneeId()));
            validateTaskAssignment(task.getProject().getId(), currentUser, newAssignee);

            String oldAssigneeName = task.getAssignee() != null ? task.getAssignee().getFullName() : "Unassigned";
            task.setAssignee(newAssignee);
            logActivity(task.getId(), "ASSIGNEE_CHANGED", oldAssigneeName, newAssignee.getFullName(), currentUser);
        }

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    public TaskResponse updateStatus(Long id, TaskStatusUpdateRequest request, Long currentUserId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        // Check dependencies if moving to COMPLETED or IN_PROGRESS
        if ("COMPLETED".equalsIgnoreCase(request.getStatus())) {
            for (Task dep : task.getDependencies()) {
                if (!"COMPLETED".equalsIgnoreCase(dep.getStatus())) {
                    throw new BadRequestException("Cannot complete task. Dependent task '" + dep.getTitle() + "' is not completed yet.");
                }
            }
        }

        String oldStatus = task.getStatus();
        task.setStatus(request.getStatus());
        task = taskRepository.save(task);

        String note = request.getNote() != null ? " (" + request.getNote() + ")" : "";
        logActivity(task.getId(), "STATUS_CHANGED", oldStatus, request.getStatus() + note, currentUser);

        return mapToResponse(task);
    }

    @Override
    public TaskResponse assignTask(Long id, TaskAssignRequest request, Long currentUserId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        User newAssignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found with id: " + request.getAssigneeId()));

        validateTaskAssignment(task.getProject().getId(), currentUser, newAssignee);

        String oldAssigneeName = task.getAssignee() != null ? task.getAssignee().getFullName() : "Unassigned";
        task.setAssignee(newAssignee);
        task = taskRepository.save(task);

        String note = request.getNote() != null ? " (" + request.getNote() + ")" : "";
        logActivity(task.getId(), "ASSIGNEE_CHANGED", oldAssigneeName, newAssignee.getFullName() + note, currentUser);

        return mapToResponse(task);
    }

    @Override
    public TaskResponse addFollower(Long taskId, Long userId, Long currentUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        task.getFollowers().add(follower);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    public TaskResponse removeFollower(Long taskId, Long userId, Long currentUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.getFollowers().removeIf(u -> u.getId().equals(userId));
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    public TaskResponse addDependency(Long taskId, Long dependsOnTaskId, Long currentUserId) {
        if (taskId.equals(dependsOnTaskId)) {
            throw new BadRequestException("A task cannot depend on itself");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        Task dependsOnTask = taskRepository.findById(dependsOnTaskId)
                .orElseThrow(() -> new ResourceNotFoundException("Dependency task not found with id: " + dependsOnTaskId));

        task.getDependencies().add(dependsOnTask);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    public TaskResponse removeDependency(Long taskId, Long dependsOnTaskId, Long currentUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.getDependencies().removeIf(t -> t.getId().equals(dependsOnTaskId));
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    /**
     * Enforce business rule from PDF:
     * - Project Leader can assign to any member in project.
     * - Project Manager can only assign to employees within their own department.
     */
    private void validateTaskAssignment(Long projectId, User assigner, User assignee) {
        // Assignee must be a member of the project
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, assignee.getId());
        if (!isMember) {
            throw new BadRequestException("Assignee (" + assignee.getFullName() + ") is not a member of this project.");
        }

        // Check assigner role in project
        ProjectMember assignerMember = projectMemberRepository.findByProjectIdAndUserId(projectId, assigner.getId())
                .orElse(null);

        if (assignerMember != null && "PROJECT_MANAGER".equalsIgnoreCase(assignerMember.getProjectRole())) {
            // Must belong to the same department (không giao chéo phòng ban)
            if (assigner.getDepartment() == null || assignee.getDepartment() == null
                    || !assigner.getDepartment().getId().equals(assignee.getDepartment().getId())) {
                throw new BadRequestException("Project Manager can only assign tasks to employees in their own department.");
            }
        }
    }

    private void logActivity(Long taskId, String action, String oldValue, String newValue, User performedBy) {
        ActivityLog log = ActivityLog.builder()
                .entityType("TASK")
                .entityId(taskId)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .performedBy(performedBy)
                .build();
        activityLogRepository.save(log);
    }

    private TaskResponse mapToResponse(Task task) {
        boolean isOverdue = task.getDeadline() != null
                && task.getDeadline().isBefore(LocalDateTime.now())
                && !"COMPLETED".equalsIgnoreCase(task.getStatus())
                && !"CANCELLED".equalsIgnoreCase(task.getStatus());

        List<TaskResponse.TaskSimpleResponse> depList = task.getDependencies().stream()
                .map(d -> TaskResponse.TaskSimpleResponse.builder()
                        .id(d.getId())
                        .title(d.getTitle())
                        .status(d.getStatus())
                        .priority(d.getPriority())
                        .build())
                .toList();

        List<UserResponse> followerList = task.getFollowers().stream()
                .map(f -> UserResponse.builder()
                        .id(f.getId())
                        .fullName(f.getFullName())
                        .email(f.getEmail())
                        .departmentId(f.getDepartment() != null ? f.getDepartment().getId() : null)
                        .systemRole(f.getSystemRole())
                        .build())
                .toList();

        int attachmentCount = attachmentRepository.findByEntityTypeAndEntityId("TASK", task.getId()).size();

        return TaskResponse.builder()
                .id(task.getId())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .assigneeName(task.getAssignee() != null ? task.getAssignee().getFullName() : null)
                .assigneeEmail(task.getAssignee() != null ? task.getAssignee().getEmail() : null)
                .createdById(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null)
                .createdByName(task.getCreatedBy() != null ? task.getCreatedBy().getFullName() : null)
                .deadline(task.getDeadline())
                .isOverdue(isOverdue)
                .dependencies(depList)
                .followers(followerList)
                .commentCount(task.getComments() != null ? task.getComments().size() : 0)
                .attachmentCount(attachmentCount)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
