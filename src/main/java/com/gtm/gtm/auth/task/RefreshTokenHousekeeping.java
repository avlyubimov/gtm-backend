package com.gtm.gtm.auth.task;

import com.gtm.gtm.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenHousekeeping {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 */15 * * * *")
    public void sweepExpired() {
        int revoked = refreshTokenRepository.revokeExpired();
        if (revoked > 0) {
            log.info("[Auth] Revoked {} expired refresh tokens", revoked);
        }
    }
}
