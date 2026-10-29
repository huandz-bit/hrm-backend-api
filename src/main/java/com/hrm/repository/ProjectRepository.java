package com.hrm.repository;

import com.hrm.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(String status);

    List<Project> findByLeaderId(Long leaderId);

    List<Project> findByCreatedById(Long createdById);

    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.user.id = :userId")
    List<Project> findProjectsByMemberUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.user.id = :userId")
    Page<Project> findProjectsByMemberUserId(@Param("userId") Long userId, Pageable pageable);

    boolean existsByName(String name);
}
