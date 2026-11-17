package com.hrm.controller;
import com.hrm.dto.ApiResponse; import com.hrm.dto.notification.NotificationResponse; import com.hrm.security.UserPrincipal; import com.hrm.service.NotificationService; import lombok.RequiredArgsConstructor; import org.springframework.http.ResponseEntity; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping({"/api/notifications","/notifications"}) @RequiredArgsConstructor public class NotificationController {
 private final NotificationService service;
 @GetMapping public ResponseEntity<ApiResponse<List<NotificationResponse>>> mine(@AuthenticationPrincipal UserPrincipal me){return ResponseEntity.ok(ApiResponse.success("Notifications retrieved",service.mine(me.getId())));}
 @PatchMapping("/{id}/read") public ResponseEntity<ApiResponse<Void>> read(@PathVariable Long id,@AuthenticationPrincipal UserPrincipal me){service.markRead(id,me.getId());return ResponseEntity.ok(ApiResponse.success("Notification marked read",null));}
 @PatchMapping("/read-all") public ResponseEntity<ApiResponse<Void>> readAll(@AuthenticationPrincipal UserPrincipal me){service.markAllRead(me.getId());return ResponseEntity.ok(ApiResponse.success("Notifications marked read",null));}
}
