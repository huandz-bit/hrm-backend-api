package com.hrm.service.impl;

import com.hrm.dto.attachment.AttachmentResponse;
import com.hrm.entity.ActivityLog;
import com.hrm.entity.Attachment;
import com.hrm.entity.User;
import com.hrm.exception.BadRequestException;
import com.hrm.exception.ResourceNotFoundException;
import com.hrm.repository.ActivityLogRepository;
import com.hrm.repository.AttachmentRepository;
import com.hrm.repository.UserRepository;
import com.hrm.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachments(String entityType, Long entityId) {
        return attachmentRepository.findByEntityTypeAndEntityId(entityType.toUpperCase(), entityId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public AttachmentResponse uploadFile(String entityType, Long entityId, MultipartFile file, Long currentUserId) {
        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path targetLocation = uploadDir.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/api/attachments/download/" + storedFilename;

            Attachment attachment = Attachment.builder()
                    .entityType(entityType.toUpperCase())
                    .entityId(entityId)
                    .fileName(originalFilename)
                    .fileUrl(fileUrl)
                    .fileSize(file.getSize())
                    .uploadedBy(user)
                    .build();

            attachment = attachmentRepository.save(attachment);

            // Log activity
            ActivityLog log = ActivityLog.builder()
                    .entityType(entityType.toUpperCase())
                    .entityId(entityId)
                    .action("ATTACHMENT_UPLOADED")
                    .newValue("Uploaded file: " + originalFilename)
                    .performedBy(user)
                    .build();
            activityLogRepository.save(log);

            return mapToResponse(attachment);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadFile(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));

        String filename = attachment.getFileUrl().replace("/api/attachments/download/", "");
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found on server: " + attachment.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found on server: " + attachment.getFileName());
        }
    }

    @Override
    public void deleteAttachment(Long id, Long currentUserId) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + id));

        String filename = attachment.getFileUrl().replace("/api/attachments/download/", "");
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {}

        attachmentRepository.delete(attachment);
    }

    private AttachmentResponse mapToResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .entityType(attachment.getEntityType())
                .entityId(attachment.getEntityId())
                .fileUrl(attachment.getFileUrl())
                .fileName(attachment.getFileName())
                .fileSize(attachment.getFileSize())
                .uploadedById(attachment.getUploadedBy().getId())
                .uploadedByName(attachment.getUploadedBy().getFullName())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
