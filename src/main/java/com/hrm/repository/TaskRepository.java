package com.hrm.repository;

import com.hrm.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    List<Task> findByProjectIdAndStatus(Long projectId, String status);

    List<Task> findByAssigneeId(Long assigneeId);

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    List<Task> findByCreatedById(Long createdById);

    @Query("SELECT t FROM Task t JOIN t.followers f WHERE f.id = :userId")
    List<Task> findByFollowerUserId(@Param("userId") Long userId);

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(Long projectId, String status);

    long countByStatus(String status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.deadline < :now AND t.status != 'COMPLETED' AND t.status != 'CANCELLED'")
    long countOverdueTasks(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.deadline < :now AND t.status != 'COMPLETED' AND t.status != 'CANCELLED'")
    long countOverdueTasksByProject(@Param("projectId") Long projectId, @Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status != 'COMPLETED' AND t.status != 'CANCELLED'")
    List<Task> findIncompleteTasksByProject(@Param("projectId") Long projectId);
}
