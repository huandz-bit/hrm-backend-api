package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "recipient_id", nullable = false) private User recipient;
    @Column(nullable = false, length = 60) private String type;
    @Column(nullable = false, length = 255) private String title;
    @Column(nullable = false, length = 1000) private String message;
    @Column(length = 255) private String link;
    @Column(name = "read_at") private LocalDateTime readAt;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
