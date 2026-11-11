package com.hrm.dto.email;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailConfigRequest {

    @NotBlank(message = "SMTP host is required")
    private String smtpHost;

    @Min(value = 1, message = "SMTP port must be valid")
    @Max(value = 65535, message = "SMTP port must be valid")
    private Integer smtpPort;

    @NotBlank(message = "Sender email is required")
    private String senderEmail;

    private String senderName;

    private String username;

    private String password;

    private Boolean enableSsl;

    private Boolean enableTls;

    private Boolean taskNotificationEnabled;

    private Boolean deadlineReminderEnabled;
}
