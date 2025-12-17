package com.gtm.gtm.auth.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gtm.gtm.auth.domain.RefreshToken;
import com.gtm.gtm.auth.dto.RegisterDto;
import com.gtm.gtm.auth.dto.SessionDto;
import com.gtm.gtm.auth.repository.RefreshTokenRepository;
import com.gtm.gtm.auth.service.AuthAuditService;
import com.gtm.gtm.auth.util.ClientInfo;
import com.gtm.gtm.config.JwtProperties;
import com.gtm.gtm.config.SecurityAuthProperties;
import com.gtm.gtm.user.repository.AppUserRepository;
import com.gtm.gtm.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Tag(name = "Auth", description = "Аутентификация и выпуск JWT")
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthenticationManager authManager;
    private final AppUserRepository repo;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties props;
    private final RefreshTokenRepository refreshRepo;
    private final UserService userService;
    private final JwtDecoder jwtDecoder;
    private final SecurityAuthProperties securityAuthProperties;
    private final AuthAuditService authAuditService;

    @Schema(name = "RefreshRequest")
    public record RefreshRequest(@NotBlank @JsonProperty("refreshToken") String refreshToken) {}

    public record LoginDto(@NotBlank String login, @NotBlank String password) {}
    public record TokenPair(String accessToken, String refreshToken, String tokenType, Integer expiresIn) {}

    public AuthController(AuthenticationManager authManager,
                          AppUserRepository repo,
                          JwtEncoder jwtEncoder,
                          JwtProperties props,
                          RefreshTokenRepository refreshRepo,
                          UserService userService,
                          JwtDecoder jwtDecoder,
                          SecurityAuthProperties securityAuthProperties,
                          AuthAuditService authAuditService) {
        this.authManager = authManager;
        this.repo = repo;
        this.jwtEncoder = jwtEncoder;
        this.props = props;
        this.refreshRepo = refreshRepo;
        this.userService = userService;
        this.jwtDecoder = jwtDecoder;
        this.securityAuthProperties = securityAuthProperties;
        this.authAuditService = authAuditService;
    }

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDto dto) {
        var created = userService.register(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Вход и получение пары токенов (access + refresh)")
    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@RequestBody @Valid LoginDto dto,
                                           HttpServletRequest request) throws UnknownHostException {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.login(), dto.password()));
        var user = repo.findByLogin(dto.login()).orElseThrow();

        var now = Instant.now();
        var accessClaims = JwtClaimsSet.builder()
                .subject(String.valueOf(user.getId()))
                .claim("roles", user.getRoles())
                .issuedAt(now)
                .expiresAt(now.plus(props.expMin(), ChronoUnit.MINUTES))
                .build();
        var access = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), accessClaims)).getTokenValue();

        var jti = UUID.randomUUID().toString();
        var refreshClaims = JwtClaimsSet.builder()
                .subject(String.valueOf(user.getId()))
                .claim("typ", "refresh")
                .claim("jti", jti)
                .issuedAt(now)
                .expiresAt(now.plus(props.refreshExpMin(), ChronoUnit.MINUTES))
                .build();
        var refresh = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), refreshClaims)).getTokenValue();

        var rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setJti(jti);
        rt.setExpiresAt(OffsetDateTime.now().plusMinutes(props.refreshExpMin()));
        String ua = request.getHeader("User-Agent");
        rt.setUserAgent(ua);
        rt.setIp(InetAddress.getByName(ClientInfo.clientIp(request)));
        rt.setDevice(ClientInfo.deviceFromUA(ua));
        refreshRepo.save(rt);
        authAuditService.success(user.getId(), dto.login(), InetAddress.getByName(ClientInfo.clientIp(request)), ua);
        refreshRepo.enforceActiveLimit(user.getId(), securityAuthProperties.getMaxSessionsPerUser());

        user.setLastLoginAt(OffsetDateTime.now());
        repo.save(user);

        return ResponseEntity.ok(new TokenPair(access, refresh, "Bearer", (int)(props.expMin()*60)));
    }

    @Operation(summary = "Обновить access по refresh-токену (с ротацией)")
    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@RequestBody @Valid RefreshRequest body,
                                             HttpServletRequest request) throws UnknownHostException {
        Jwt jwt = jwtDecoder.decode(body.refreshToken());
        if (!"refresh".equals(jwt.getClaimAsString("typ"))) {
            throw new JwtException("Not a refresh token");
        }
        String jti = jwt.getClaimAsString("jti");
        Long userId = Long.parseLong(jwt.getSubject());

        var stored = refreshRepo.findByJtiAndRevokedFalse(jti)
                .orElseThrow(() -> new JwtException("Refresh token is revoked or unknown"));
        if (stored.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new JwtException("Refresh token expired");
        }
        if (!stored.getUserId().equals(userId)) {
            throw new JwtException("Invalid refresh owner");
        }

        stored.setRevoked(true);
        refreshRepo.save(stored);

        var now = Instant.now();

        var accessClaims = JwtClaimsSet.builder()
                .subject(String.valueOf(userId))
                .claim("roles", repo.findById(userId).orElseThrow().getRoles())
                .issuedAt(now)
                .expiresAt(now.plus(props.expMin(), ChronoUnit.MINUTES))
                .build();
        var access = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), accessClaims)).getTokenValue();

        var newJti = UUID.randomUUID().toString();
        var refreshClaims = JwtClaimsSet.builder()
                .subject(String.valueOf(userId))
                .claim("typ", "refresh")
                .claim("jti", newJti)
                .issuedAt(now)
                .expiresAt(now.plus(props.refreshExpMin(), ChronoUnit.MINUTES))
                .build();
        var newRefresh = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), refreshClaims)).getTokenValue();

        var rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setJti(newJti);
        rt.setExpiresAt(OffsetDateTime.now().plusMinutes(props.refreshExpMin()));
        String ua = request.getHeader("User-Agent");
        rt.setUserAgent(ua);
        rt.setIp(InetAddress.getByName(ClientInfo.clientIp(request)));
        rt.setDevice(ClientInfo.deviceFromUA(ua));
        refreshRepo.save(rt);
        refreshRepo.enforceActiveLimit(userId, securityAuthProperties.getMaxSessionsPerUser());


        return ResponseEntity.ok(new TokenPair(access, newRefresh, "Bearer", (int)(props.expMin()*60)));
    }

    @Operation(summary = "Активные сессии пользователя")
    @GetMapping("/sessions")
    @PreAuthorize("isAuthenticated()")
    public List<SessionDto> sessions(@AuthenticationPrincipal Jwt jwt) {
        return refreshRepo
                .findAllByUserIdAndRevokedFalseOrderByCreatedAtDesc(Long.valueOf(jwt.getSubject()))
                .stream()
                .map(r -> new SessionDto(
                        r.getJti(),
                        r.getCreatedAt(),
                        r.getExpiresAt(),
                        r.getIp(),
                        r.getDevice(),
                        r.getUserAgent(),
                        r.isRevoked()
                ))
                .toList();
    }

    @Operation(summary = "Выход с одного устройства (revoke по jti)")
    @PostMapping("/logout/one")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logoutOne(@RequestParam String jti,
                                          @AuthenticationPrincipal Jwt jwt) {
        refreshRepo.findByJtiAndRevokedFalse(jti)
                .filter(r -> r.getUserId().equals(Long.valueOf(jwt.getSubject())))
                .ifPresent(r -> { r.setRevoked(true); refreshRepo.save(r); });
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Выход везде (revoke всех refresh)")
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Void> logoutEverywhere(@AuthenticationPrincipal Jwt jwt) {
        refreshRepo.revokeAllByUserId(Long.valueOf(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Выход на всех устройствах, кроме текущего (оставить сессию с переданным jti)")
    @PostMapping("/logout/others")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Void> logoutOthers(@RequestParam String jti,
                                             @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        // Убедимся, что переданный jti принадлежит текущему пользователю и не отозван
        var cur = refreshRepo.findByJtiAndRevokedFalse(jti)
                .filter(r -> r.getUserId().equals(userId))
                .orElseThrow(() -> new com.gtm.gtm.common.error.NotFoundException("Refresh token not found for current user"));
        refreshRepo.revokeAllExceptJti(userId, cur.getJti());
        return ResponseEntity.noContent().build();
    }
}
