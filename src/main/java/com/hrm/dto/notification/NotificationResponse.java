package com.hrm.dto.notification;
import lombok.*; import java.time.LocalDateTime;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor public class NotificationResponse { private Long id; private String type; private String title; private String message; private String link; private boolean read; private LocalDateTime createdAt; }
