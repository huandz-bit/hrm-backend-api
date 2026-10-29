package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.attachment.AttachmentResponse;
import com.hrm.security.UserPrincipal;
import com.hrm.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Attachments", description = "File Upload and Attachment management APIs for Task and Project")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping
    @Operation(summary = "Get attachments for an entity (TASK or PROJECT)")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> getAttachments(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        List<AttachmentResponse> list = attachmentService.getAttachments(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success("Attachments retrieved successfully", list));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file attachment to Task or Project")
    public ResponseEntity<ApiResponse<AttachmentResponse>> uploadFile(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        AttachmentResponse response = attachmentService.uploadFile(entityType, entityId, file, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Download file attachment by ID")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Resource resource = attachmentService.downloadFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attachment")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : 1L;
        attachmentService.deleteAttachment(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Attachment deleted successfully", null));
    }
}
