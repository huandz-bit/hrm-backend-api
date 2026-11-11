package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.email.EmailConfigRequest;
import com.hrm.dto.email.EmailConfigResponse;
import com.hrm.service.EmailConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/email-config", "/email-config"})
@RequiredArgsConstructor
@Validated
@Tag(name = "Email Configuration", description = "System email and SMTP notification settings")
public class EmailConfigController {

    private final EmailConfigService emailConfigService;

    @GetMapping
    @Operation(summary = "Get system email configuration")
    public ResponseEntity<ApiResponse<EmailConfigResponse>> getConfig() {
        EmailConfigResponse config = emailConfigService.getConfig();
        return ResponseEntity.ok(ApiResponse.success("Email configuration retrieved successfully", config));
    }

    @PutMapping
    @Operation(summary = "Update system email configuration")
    public ResponseEntity<ApiResponse<EmailConfigResponse>> updateConfig(
            @Valid @RequestBody EmailConfigRequest request) {
        EmailConfigResponse config = emailConfigService.updateConfig(request);
        return ResponseEntity.ok(ApiResponse.success("Email configuration updated successfully", config));
    }
}
