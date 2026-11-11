package com.hrm.service;

import com.hrm.dto.hr.HrScorecardResponse;

import java.util.List;

public interface HrService {

    List<HrScorecardResponse> getScorecards(Long departmentId);
}
