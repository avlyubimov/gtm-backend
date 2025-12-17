package com.gtm.gtm.notification.service;

import com.gtm.gtm.common.error.NotFoundException;
import com.gtm.gtm.notification.domain.Notification;
import com.gtm.gtm.notification.domain.NotificationType;
import com.gtm.gtm.notification.dto.NotificationDto;
import com.gtm.gtm.notification.repository.NotificationRepository;
import com.gtm.gtm.notification.sse.NotificationBroadcaster;
import com.gtm.gtm.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repo;
    private final AppUserRepository userRepo;
    private final NotificationBroadcaster broadcaster;

    @Transactional
    public NotificationDto createForUser(Long userId, NotificationType type, String title, String message, Map<String, Object> payload) {
        var n = new Notification();
        n.setUser(userRepo.getReferenceById(userId));
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setPayload(payload);
        n.setRead(false);
        var now = OffsetDateTime.now();
        n.setCreatedAt(now);
        n.setUpdatedAt(now);

        var saved = repo.save(n);
        var dto = toDto(saved);
        // push to SSE listeners
        broadcaster.send(userId, dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> pageForUser(Long userId, Boolean isRead, NotificationType type, Pageable pageable) {
        if (type != null && isRead != null) {
            return repo.findAllByUser_IdAndTypeAndRead(userId, type, isRead, pageable).map(this::toDto);
        }
        if (type != null) {
            return repo.findAllByUser_IdAndType(userId, type, pageable).map(this::toDto);
        }
        if (isRead != null) {
            return repo.findAllByUser_IdAndRead(userId, isRead, pageable).map(this::toDto);
        }
        return repo.findAllByUser_Id(userId, pageable).map(this::toDto);
    }

    @Transactional
    public void markRead(Long userId, Long id) {
        var n = repo.findByIdAndUser_Id(id, userId).orElseThrow(() -> new NotFoundException("Notification not found"));
        if (!n.isRead()) {
            n.setRead(true);
            n.setUpdatedAt(OffsetDateTime.now());
            repo.save(n);
        }
    }

    @Transactional
    public void markAllReadForUser(Long userId) {
        repo.markAllReadForUser(userId);
    }

    private NotificationDto toDto(Notification n) {
        return new NotificationDto(
                n.getId(),
                n.getType(),
                n.getTitle(),
                n.getMessage(),
                n.getPayload(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
