package com.hrm.repository;
import com.hrm.entity.ProfileChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProfileChangeRequestRepository extends JpaRepository<ProfileChangeRequest, Long> { List<ProfileChangeRequest> findByStatusOrderByCreatedAtDesc(String status); List<ProfileChangeRequest> findAllByOrderByCreatedAtDesc(); }
