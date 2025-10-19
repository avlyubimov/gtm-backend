package com.gtm.gtm.auth.service;

import com.gtm.gtm.auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenHousekeeping {
    private final RefreshTokenRepository repo;

    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void revokeExpired() {
        repo.revokeExpired();
    }
}
