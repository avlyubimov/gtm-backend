package com.gtm.gtm.notification.controller;

import com.gtm.gtm.common.security.CurrentUser;
import com.gtm.gtm.notification.domain.NotificationType;
import com.gtm.gtm.notification.dto.NotificationDto;
import com.gtm.gtm.notification.service.NotificationService;
import com.gtm.gtm.notification.sse.NotificationBroadcaster;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Notifications", description = "Уведомления")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;
    private final NotificationBroadcaster broadcaster;
    private final CurrentUser currentUser;

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    public Page<NotificationDto> my(Pageable pageable,
                                    @RequestParam(required = false) Boolean isRead,
                                    @RequestParam(required = false) NotificationType type) {
        return service.pageForUser(currentUser.id(), isRead, type, pageable);
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable Long id) {
        service.markRead(currentUser.id(), id);
    }

    @PostMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead() {
        service.markAllReadForUser(currentUser.id());
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KAMERAL')")
    public SseEmitter stream() {
        return broadcaster.subscribe(currentUser.id());
    }
}
