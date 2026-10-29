package com.hrm.repository;

import com.hrm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByDepartmentId(Long departmentId);

    Page<User> findByDepartmentId(Long departmentId, Pageable pageable);

    List<User> findBySystemRole(String systemRole);

    List<User> findByStatus(String status);
}
