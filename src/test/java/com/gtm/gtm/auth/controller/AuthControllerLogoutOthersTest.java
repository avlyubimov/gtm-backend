package com.gtm.gtm.auth.controller;

import com.gtm.gtm.auth.domain.RefreshToken;
import com.gtm.gtm.auth.repository.RefreshTokenRepository;
import com.gtm.gtm.auth.service.AuthAuditService;
import com.gtm.gtm.config.JwtProperties;
import com.gtm.gtm.config.SecurityAuthProperties;
import com.gtm.gtm.user.repository.AppUserRepository;
import com.gtm.gtm.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class AuthControllerLogoutOthersTest {

    @Test
    void logoutOthers_revokesAllExceptProvidedJti() {
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        AppUserRepository userRepo = mock(AppUserRepository.class);
        JwtEncoder jwtEncoder = mock(JwtEncoder.class);
        JwtProperties props = mock(JwtProperties.class);
        RefreshTokenRepository refreshRepo = mock(RefreshTokenRepository.class);
        UserService userService = mock(UserService.class);
        JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        SecurityAuthProperties securityProps = mock(SecurityAuthProperties.class);
        AuthAuditService audit = mock(AuthAuditService.class);

        var controller = new AuthController(authManager, userRepo, jwtEncoder, props, refreshRepo, userService, jwtDecoder, securityProps, audit);

        // Prepare current user and his current refresh token jti
        String jti = "cur-jti";
        var rt = new RefreshToken();
        rt.setJti(jti);
        rt.setUserId(1L);
        rt.setExpiresAt(OffsetDateTime.now().plusDays(1));
        when(refreshRepo.findByJtiAndRevokedFalse(jti)).thenReturn(Optional.of(rt));

        Jwt jwt = Jwt.withTokenValue("t").subject("1").header("alg", "none").build();

        controller.logoutOthers(jti, jwt);

        verify(refreshRepo, times(1)).findByJtiAndRevokedFalse(jti);
        verify(refreshRepo, times(1)).revokeAllExceptJti(1L, jti);
        verifyNoMoreInteractions(refreshRepo);
    }
}
