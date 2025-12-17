package com.gtm.gtm.auth.task;

import com.gtm.gtm.auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class RefreshTokenHousekeepingTest {

    @Test
    void sweepExpired_callsRepository() {
        var repo = mock(RefreshTokenRepository.class);
        when(repo.revokeExpired()).thenReturn(3);

        var task = new RefreshTokenHousekeeping(repo);
        task.sweepExpired();

        verify(repo, times(1)).revokeExpired();
        verifyNoMoreInteractions(repo);
    }
}
