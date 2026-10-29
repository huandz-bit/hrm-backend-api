package com.hrm.service;

import com.hrm.dto.department.DepartmentRequest;
import com.hrm.dto.department.DepartmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {

    Page<DepartmentResponse> getAll(Pageable pageable);

    List<DepartmentResponse> getAllList();

    DepartmentResponse getById(Long id);

    DepartmentResponse create(DepartmentRequest request);

    DepartmentResponse update(Long id, DepartmentRequest request);

    void delete(Long id);
}
