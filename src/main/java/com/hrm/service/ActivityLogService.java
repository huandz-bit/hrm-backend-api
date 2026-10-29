package com.hrm.service;

import com.hrm.dto.activity.ActivityLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityLogService {

    List<ActivityLogResponse> getLogs(String entityType, Long entityId);

    Page<ActivityLogResponse> getLogsPage(String entityType, Long entityId, Pageable pageable);
}
