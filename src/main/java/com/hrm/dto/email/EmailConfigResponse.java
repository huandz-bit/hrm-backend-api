package com.hrm.dto.email;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailConfigResponse {

    private String smtpHost;
    private Integer smtpPort;
    private String senderEmail;
    private String senderName;
    private String username;
    private Boolean enableSsl;
    private Boolean enableTls;
    private Boolean taskNotificationEnabled;
    private Boolean deadlineReminderEnabled;
    private LocalDateTime updatedAt;
}
