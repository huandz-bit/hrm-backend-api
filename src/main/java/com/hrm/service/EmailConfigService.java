package com.hrm.service;

import com.hrm.dto.email.EmailConfigRequest;
import com.hrm.dto.email.EmailConfigResponse;

public interface EmailConfigService {

    EmailConfigResponse getConfig();

    EmailConfigResponse updateConfig(EmailConfigRequest request);
}
