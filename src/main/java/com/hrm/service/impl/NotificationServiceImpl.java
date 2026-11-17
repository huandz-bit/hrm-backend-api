package com.hrm.service.impl;
import com.hrm.dto.notification.NotificationResponse; import com.hrm.entity.Notification; import com.hrm.entity.User; import com.hrm.exception.ResourceNotFoundException; import com.hrm.repository.NotificationRepository; import com.hrm.repository.UserRepository; import com.hrm.service.NotificationService;
import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.time.LocalDateTime; import java.util.List;
@Service @RequiredArgsConstructor @Transactional public class NotificationServiceImpl implements NotificationService {
 private final NotificationRepository notifications; private final UserRepository users;
 public List<NotificationResponse> mine(Long userId) { return notifications.findByRecipientIdOrderByCreatedAtDesc(userId).stream().map(this::map).toList(); }
 public void markRead(Long id, Long userId) { Notification n=notifications.findById(id).orElseThrow(()->new ResourceNotFoundException("Notification not found")); if(!n.getRecipient().getId().equals(userId)) throw new IllegalArgumentException("No permission"); n.setReadAt(LocalDateTime.now()); }
 public void markAllRead(Long userId) { notifications.findByRecipientIdOrderByCreatedAtDesc(userId).forEach(n->n.setReadAt(LocalDateTime.now())); }
 public void create(Long userId,String type,String title,String message,String link) { User u=users.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found")); notifications.save(Notification.builder().recipient(u).type(type).title(title).message(message).link(link).build()); }
 private NotificationResponse map(Notification n){return NotificationResponse.builder().id(n.getId()).type(n.getType()).title(n.getTitle()).message(n.getMessage()).link(n.getLink()).read(n.getReadAt()!=null).createdAt(n.getCreatedAt()).build();}
}
