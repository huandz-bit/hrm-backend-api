package com.hrm.dto.attachment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentResponse {

    private Long id;
    private String entityType;
    private Long entityId;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime createdAt;
}
