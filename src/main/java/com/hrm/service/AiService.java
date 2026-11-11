package com.hrm.service;

import com.hrm.dto.ai.ProjectPlanRequest;
import com.hrm.dto.ai.ProjectPlanResponse;

public interface AiService {

    ProjectPlanResponse generateProjectPlan(ProjectPlanRequest request);
}
