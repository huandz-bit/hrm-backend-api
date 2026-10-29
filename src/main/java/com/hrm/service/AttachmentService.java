package com.hrm.service;

import com.hrm.dto.attachment.AttachmentResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    List<AttachmentResponse> getAttachments(String entityType, Long entityId);

    AttachmentResponse uploadFile(String entityType, Long entityId, MultipartFile file, Long currentUserId);

    Resource downloadFile(Long attachmentId);

    void deleteAttachment(Long id, Long currentUserId);
}
