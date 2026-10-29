package com.hrm.service.impl;

import com.hrm.dto.project.ProjectRequest;
import com.hrm.dto.project.ProjectResponse;
import com.hrm.dto.projectmember.ProjectMemberRequest;
import com.hrm.dto.projectmember.ProjectMemberResponse;
import com.hrm.entity.*;
import com.hrm.exception.BadRequestException;
import com.hrm.exception.ResourceNotFoundException;
import com.hrm.repository.*;
import com.hrm.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAll(Pageable pageable) {
        return projectRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(Long currentUserId) {
        return projectRepository.findProjectsByMemberUserId(currentUserId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return mapToResponse(project);
    }

    @Override
    public ProjectResponse create(ProjectRequest request, Long currentUserId) {
        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        User leader = creator;
        if (request.getLeaderId() != null) {
            leader = userRepository.findById(request.getLeaderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leader user not found with id: " + request.getLeaderId()));
        }

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .leader(leader)
                .status(request.getStatus() != null ? request.getStatus() : "PLANNING")
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now())
                .endDate(request.getEndDate())
                .createdBy(creator)
                .build();

        project = projectRepository.save(project);

        // Auto-add leader as PROJECT_LEADER in project_members
        ProjectMember leaderMember = ProjectMember.builder()
                .id(new ProjectMemberId(project.getId(), leader.getId()))
                .project(project)
                .user(leader)
                .projectRole("PROJECT_LEADER")
                .joinedAt(LocalDateTime.now())
                .build();
        projectMemberRepository.save(leaderMember);

        // If creator is not the leader, add creator as PROJECT_MEMBER
        if (!creator.getId().equals(leader.getId())) {
            ProjectMember creatorMember = ProjectMember.builder()
                    .id(new ProjectMemberId(project.getId(), creator.getId()))
                    .project(project)
                    .user(creator)
                    .projectRole("PROJECT_MEMBER")
                    .joinedAt(LocalDateTime.now())
                    .build();
            projectMemberRepository.save(creatorMember);
        }

        // Log Activity
        ActivityLog log = ActivityLog.builder()
                .entityType("PROJECT")
                .entityId(project.getId())
                .action("CREATED")
                .newValue("Project created with status " + project.getStatus())
                .performedBy(creator)
                .build();
        activityLogRepository.save(log);

        return mapToResponse(project);
    }

    @Override
    public ProjectResponse update(Long id, ProjectRequest request, Long currentUserId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        if (request.getLeaderId() != null && !request.getLeaderId().equals(project.getLeader().getId())) {
            User newLeader = userRepository.findById(request.getLeaderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leader user not found with id: " + request.getLeaderId()));
            project.setLeader(newLeader);

            // Update or add new leader in project_members
            ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(project.getId(), newLeader.getId())
                    .orElse(ProjectMember.builder()
                            .id(new ProjectMemberId(project.getId(), newLeader.getId()))
                            .project(project)
                            .user(newLeader)
                            .build());
            member.setProjectRole("PROJECT_LEADER");
            projectMemberRepository.save(member);
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }

        project = projectRepository.save(project);

        ActivityLog log = ActivityLog.builder()
                .entityType("PROJECT")
                .entityId(project.getId())
                .action("UPDATED")
                .newValue("Project details updated")
                .performedBy(currentUser)
                .build();
        activityLogRepository.save(log);

        return mapToResponse(project);
    }

    @Override
    public ProjectResponse closeProject(Long id, Long currentUserId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        // Check if there are incomplete tasks
        List<Task> incompleteTasks = taskRepository.findIncompleteTasksByProject(id);
        if (!incompleteTasks.isEmpty()) {
            throw new BadRequestException("Cannot close project. There are " + incompleteTasks.size() + " incomplete tasks remaining.");
        }

        project.setStatus("CLOSED");
        project = projectRepository.save(project);

        ActivityLog log = ActivityLog.builder()
                .entityType("PROJECT")
                .entityId(project.getId())
                .action("CLOSED")
                .oldValue(project.getStatus())
                .newValue("CLOSED")
                .performedBy(currentUser)
                .build();
        activityLogRepository.save(log);

        return mapToResponse(project);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        project.setStatus("CANCELLED");
        projectRepository.save(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMembers(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        return projectMemberRepository.findByProjectId(projectId)
                .stream().map(this::mapMemberToResponse).toList();
    }

    @Override
    public ProjectMemberResponse addMember(Long projectId, ProjectMemberRequest request, Long currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new BadRequestException("User is already a member of this project");
        }

        // Rule: only 1 leader at a time
        if ("PROJECT_LEADER".equalsIgnoreCase(request.getProjectRole())) {
            throw new BadRequestException("Project can only have one Project Leader at a time. Update project leader instead.");
        }

        ProjectMember member = ProjectMember.builder()
                .id(new ProjectMemberId(projectId, user.getId()))
                .project(project)
                .user(user)
                .projectRole(request.getProjectRole())
                .joinedAt(LocalDateTime.now())
                .build();

        member = projectMemberRepository.save(member);
        return mapMemberToResponse(member);
    }

    @Override
    public ProjectMemberResponse updateMemberRole(Long projectId, Long userId, String newRole, Long currentUserId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project member not found for user: " + userId));

        if ("PROJECT_LEADER".equalsIgnoreCase(newRole)) {
            throw new BadRequestException("Cannot assign PROJECT_LEADER role here. Use update project leader.");
        }

        member.setProjectRole(newRole);
        member = projectMemberRepository.save(member);
        return mapMemberToResponse(member);
    }

    @Override
    public void removeMember(Long projectId, Long userId, Long currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (project.getLeader().getId().equals(userId)) {
            throw new BadRequestException("Cannot remove the Project Leader from the project.");
        }

        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    private ProjectResponse mapToResponse(Project project) {
        long totalTasks = taskRepository.countByProjectId(project.getId());
        long completedTasks = taskRepository.countByProjectIdAndStatus(project.getId(), "COMPLETED");
        double progress = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100 : 0.0;

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .leaderId(project.getLeader() != null ? project.getLeader().getId() : null)
                .leaderName(project.getLeader() != null ? project.getLeader().getFullName() : null)
                .createdById(project.getCreatedBy() != null ? project.getCreatedBy().getId() : null)
                .createdByName(project.getCreatedBy() != null ? project.getCreatedBy().getFullName() : null)
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .memberCount(project.getMembers() != null ? project.getMembers().size() : 0)
                .totalTasks((int) totalTasks)
                .completedTasks((int) completedTasks)
                .progressPercentage(Math.round(progress * 10.0) / 10.0)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    private ProjectMemberResponse mapMemberToResponse(ProjectMember member) {
        return ProjectMemberResponse.builder()
                .projectId(member.getProject().getId())
                .userId(member.getUser().getId())
                .fullName(member.getUser().getFullName())
                .email(member.getUser().getEmail())
                .departmentId(member.getUser().getDepartment() != null ? member.getUser().getDepartment().getId() : null)
                .departmentName(member.getUser().getDepartment() != null ? member.getUser().getDepartment().getName() : null)
                .projectRole(member.getProjectRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
