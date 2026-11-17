package com.hrm.service;
import com.hrm.dto.notification.NotificationResponse;
import java.util.List;
public interface NotificationService { List<NotificationResponse> mine(Long userId); void markRead(Long id, Long userId); void markAllRead(Long userId); void create(Long userId, String type, String title, String message, String link); }
