package com.hrm.service;

import com.hrm.dto.project.ProjectRequest;
import com.hrm.dto.project.ProjectResponse;
import com.hrm.dto.projectmember.ProjectMemberRequest;
import com.hrm.dto.projectmember.ProjectMemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {

    Page<ProjectResponse> getAll(Pageable pageable);

    List<ProjectResponse> getMyProjects(Long currentUserId);

    ProjectResponse getById(Long id);

    ProjectResponse create(ProjectRequest request, Long currentUserId);

    ProjectResponse update(Long id, ProjectRequest request, Long currentUserId);

    ProjectResponse closeProject(Long id, Long currentUserId);

    void delete(Long id, Long currentUserId);

    List<ProjectMemberResponse> getMembers(Long projectId);

    ProjectMemberResponse addMember(Long projectId, ProjectMemberRequest request, Long currentUserId);

    ProjectMemberResponse updateMemberRole(Long projectId, Long userId, String newRole, Long currentUserId);

    void removeMember(Long projectId, Long userId, Long currentUserId);
}
