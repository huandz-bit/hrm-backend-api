package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity @Table(name = "profile_change_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileChangeRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "requester_id", nullable = false) private User requester;
    @Column(nullable = false, length = 20) @Builder.Default private String status = "PENDING";
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "requested_data", columnDefinition = "jsonb", nullable = false) @Builder.Default private Map<String, Object> requestedData = new HashMap<>();
    @Column(length = 1000) private String reviewNote;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reviewed_by") private User reviewedBy;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "reviewed_at") private LocalDateTime reviewedAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
