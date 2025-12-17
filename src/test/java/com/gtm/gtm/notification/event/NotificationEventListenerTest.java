package com.gtm.gtm.notification.event;

import com.gtm.gtm.notification.domain.NotificationType;
import com.gtm.gtm.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NotificationEventListenerTest {

    @Test
    void onAssignmentCreated_dispatchesNotification() {
        var service = Mockito.mock(NotificationService.class);
        var listener = new NotificationEventListener(service);

        var e = new AssignmentCreatedEvent(1L, 2L, 3L, "SiteName", "S-001");
        listener.onAssignmentCreated(e);

        verify(service, times(1)).createForUser(eq(2L), eq(NotificationType.ASSIGNMENT_CREATED), anyString(), anyString(), any());
    }

    @Test
    void onAssignmentDeactivated_dispatchesNotification() {
        var service = Mockito.mock(NotificationService.class);
        var listener = new NotificationEventListener(service);

        var e = new AssignmentDeactivatedEvent(10L, 20L, 30L, "SiteName", "S-001");
        listener.onAssignmentDeactivated(e);

        verify(service, times(1)).createForUser(eq(20L), eq(NotificationType.ASSIGNMENT_DEACTIVATED), anyString(), anyString(), any());
    }
}
