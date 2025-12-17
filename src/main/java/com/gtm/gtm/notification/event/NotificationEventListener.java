package com.gtm.gtm.notification.event;

import com.gtm.gtm.notification.domain.NotificationType;
import com.gtm.gtm.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAssignmentCreated(AssignmentCreatedEvent e) {
        var payload = Map.<String, Object>of(
                "assignmentId", e.assignmentId(),
                "siteId", e.siteId(),
                "siteName", e.siteName(),
                "siteCode", e.siteCode()
        );
        String title = "Назначение участка";
        String message = "Вам назначен участок %s %s".formatted(e.siteCode(), e.siteName());
        notificationService.createForUser(e.userId(), NotificationType.ASSIGNMENT_CREATED, title, message, payload);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAssignmentDeactivated(AssignmentDeactivatedEvent e) {
        var payload = Map.<String, Object>of(
                "assignmentId", e.assignmentId(),
                "siteId", e.siteId(),
                "siteName", e.siteName(),
                "siteCode", e.siteCode()
        );
        String title = "Деактивация назначения";
        String message = "Ваше назначение по участку %s %s деактивировано".formatted(e.siteCode(), e.siteName());
        notificationService.createForUser(e.userId(), NotificationType.ASSIGNMENT_DEACTIVATED, title, message, payload);
    }
}
