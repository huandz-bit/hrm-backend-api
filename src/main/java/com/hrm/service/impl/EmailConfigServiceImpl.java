package com.hrm.service.impl;

import com.hrm.dto.email.EmailConfigRequest;
import com.hrm.dto.email.EmailConfigResponse;
import com.hrm.service.EmailConfigService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailConfigServiceImpl implements EmailConfigService {

    // In-memory persistent config defaults (can also be saved to a system_config table)
    private String smtpHost = "smtp.gmail.com";
    private Integer smtpPort = 587;
    private String senderEmail = "no-reply@hrm-enterprise.com";
    private String senderName = "HRM Notification Service";
    private String username = "hrm-enterprise@gmail.com";
    private Boolean enableSsl = false;
    private Boolean enableTls = true;
    private Boolean taskNotificationEnabled = true;
    private Boolean deadlineReminderEnabled = true;
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Override
    public EmailConfigResponse getConfig() {
        return EmailConfigResponse.builder()
                .smtpHost(this.smtpHost)
                .smtpPort(this.smtpPort)
                .senderEmail(this.senderEmail)
                .senderName(this.senderName)
                .username(this.username)
                .enableSsl(this.enableSsl)
                .enableTls(this.enableTls)
                .taskNotificationEnabled(this.taskNotificationEnabled)
                .deadlineReminderEnabled(this.deadlineReminderEnabled)
                .updatedAt(this.updatedAt)
                .build();
    }

    @Override
    public EmailConfigResponse updateConfig(EmailConfigRequest request) {
        if (request.getSmtpHost() != null) this.smtpHost = request.getSmtpHost();
        if (request.getSmtpPort() != null) this.smtpPort = request.getSmtpPort();
        if (request.getSenderEmail() != null) this.senderEmail = request.getSenderEmail();
        if (request.getSenderName() != null) this.senderName = request.getSenderName();
        if (request.getUsername() != null) this.username = request.getUsername();
        if (request.getEnableSsl() != null) this.enableSsl = request.getEnableSsl();
        if (request.getEnableTls() != null) this.enableTls = request.getEnableTls();
        if (request.getTaskNotificationEnabled() != null) this.taskNotificationEnabled = request.getTaskNotificationEnabled();
        if (request.getDeadlineReminderEnabled() != null) this.deadlineReminderEnabled = request.getDeadlineReminderEnabled();
        this.updatedAt = LocalDateTime.now();

        return getConfig();
    }
}
