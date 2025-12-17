package com.gtm.gtm.notification.service;

import com.gtm.gtm.notification.domain.Notification;
import com.gtm.gtm.notification.repository.NotificationRepository;
import com.gtm.gtm.notification.sse.NotificationBroadcaster;
import com.gtm.gtm.user.domain.AppUser;
import com.gtm.gtm.user.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Test
    void markRead_updatesOnlyOnce() {
        var repo = Mockito.mock(NotificationRepository.class);
        var userRepo = Mockito.mock(AppUserRepository.class);
        var broadcaster = Mockito.mock(NotificationBroadcaster.class);
        var service = new NotificationService(repo, userRepo, broadcaster);

        var n = new Notification();
        n.setId(5L);
        var u = new AppUser(); u.setId(2L); n.setUser(u);
        n.setRead(false);
        when(repo.findByIdAndUser_Id(5L, 2L)).thenReturn(Optional.of(n));

        service.markRead(2L, 5L);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repo, times(1)).save(captor.capture());
        assertTrue(captor.getValue().isRead());
    }

    @Test
    void markAllRead_executesBulkUpdate() {
        var repo = Mockito.mock(NotificationRepository.class);
        var userRepo = Mockito.mock(AppUserRepository.class);
        var broadcaster = Mockito.mock(NotificationBroadcaster.class);
        var service = new NotificationService(repo, userRepo, broadcaster);

        service.markAllReadForUser(99L);
        verify(repo, times(1)).markAllReadForUser(99L);
    }
}
