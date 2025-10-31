package com.gtm.gtm.user.service;

import com.gtm.gtm.auth.dto.RegisterDto;
import com.gtm.gtm.auth.repository.RefreshTokenRepository;
import com.gtm.gtm.user.domain.AppUser;
import com.gtm.gtm.user.domain.UserRole;
import com.gtm.gtm.user.domain.UserStatus;
import com.gtm.gtm.user.dto.*;
import com.gtm.gtm.user.repository.AppUserRepository;
import com.gtm.gtm.user.repository.UserSpecs;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class UserService {
    private final AppUserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(AppUserRepository repo, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private static String requireE164(String phone) {
        var p = phone == null ? "" : phone.trim();
        if (!p.matches("^\\+\\d{7,15}$"))
            throw new IllegalArgumentException("Phone must be E.164, e.g. +77011234567");
        return p;
    }

    private static UserDto toDto(AppUser u) {
        return new UserDto(
                u.getId(), u.getFullName(), u.getPhone(), u.getEmail(), u.getUsername(),
                u.getRoles(), u.getStatus(), u.getCreatedAt(), u.getUpdatedAt(), u.getLastLoginAt(),
                u.getDateOfBirth(), u.getAge(), u.getPhotoUrl()
        );
    }

    private static java.util.Optional<Long> parseAsLong(String val) {
        try { return java.util.Optional.of(Long.parseLong(val)); }
        catch (Exception ignored) { return java.util.Optional.empty(); }
    }

    @Transactional
    public UserDto create(UserCreateDto dto) {
        if (repo.existsByEmailIgnoreCase(dto.email()))
            throw new IllegalArgumentException("Email already in use");
        if (repo.existsByUsernameIgnoreCase(dto.username()))
            throw new IllegalArgumentException("Username already in use");

        String phone = requireE164(dto.phone());
        if (repo.existsByPhone(phone))
            throw new IllegalArgumentException("Phone already in use");

        var u = new AppUser();
        u.setFullName(dto.fullName().trim());
        u.setPhone(phone);
        u.setEmail(dto.email().trim());
        u.setUsername(dto.username().trim());
        u.setPasswordHash(passwordEncoder.encode(dto.password()));
        u.setStatus(UserStatus.ACTIVE);
        u.setDateOfBirth(dto.dateOfBirth());
        u.setRoles((dto.roles()==null || dto.roles().isEmpty())
                ? EnumSet.of(UserRole.KAMERAL) : EnumSet.copyOf(dto.roles()));

        return toDto(repo.save(u));
    }

    public UserDto get(Long id) {
        return repo.findById(id).map(UserService::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Page<UserDto> list(Pageable pageable) {
        return repo.findAll(pageable).map(UserService::toDto);
    }

    public UserDto getBySubject(String sub) {
        var user = parseAsLong(sub).flatMap(repo::findById)
                .or(() -> repo.findByEmailIgnoreCase(sub))
                .orElseThrow(() -> new IllegalArgumentException("User not found by subject"));
        return toDto(user);
    }

    @Transactional
    public UserDto updateSelfBySubject(String sub, UserSelfUpdateDto dto) {
        var u = parseAsLong(sub).flatMap(repo::findById)
                .or(() -> repo.findByEmailIgnoreCase(sub))
                .orElseThrow(() -> new IllegalArgumentException("User not found by subject"));

        if (repo.existsByEmailIgnoreCaseAndIdNot(dto.email().trim(), u.getId()))
            throw new IllegalArgumentException("Email already in use");
        var phone = requireE164(dto.phone());
        if (repo.existsByPhoneAndIdNot(phone, u.getId()))
            throw new IllegalArgumentException("Phone already in use");

        u.setFullName(dto.fullName().trim());
        u.setPhone(phone);
        u.setEmail(dto.email().trim());
        u.setDateOfBirth(dto.dateOfBirth());
        u.setPhotoUrl(dto.photoUrl());
        repo.saveAndFlush(u);
        return toDto(u);
    }

    @Transactional
    public UserDto adminUpdate(Long id, UserAdminUpdateDto dto) {
        var u = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (repo.existsByEmailIgnoreCaseAndIdNot(dto.email().trim(), id))
            throw new IllegalArgumentException("Email already in use");
        if (repo.existsByUsernameIgnoreCaseAndIdNot(dto.username().trim(), id))
            throw new IllegalArgumentException("Username already in use");
        var phone = requireE164(dto.phone());
        if (repo.existsByPhoneAndIdNot(phone, id))
            throw new IllegalArgumentException("Phone already in use");

        u.setFullName(dto.fullName().trim());
        u.setPhone(phone);
        u.setEmail(dto.email().trim());
        u.setUsername(dto.username().trim());
        u.setDateOfBirth(dto.dateOfBirth());
        u.setPhotoUrl(dto.photoUrl());
        if (dto.roles()!=null && !dto.roles().isEmpty()) u.setRoles(EnumSet.copyOf(dto.roles()));
        if (dto.status()!=null) u.setStatus(dto.status());

        repo.saveAndFlush(u);
        return toDto(u);
    }

    @Transactional
    public void changeStatus(Long id, UserStatus status) {
        var u = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        u.setStatus(status);
        repo.saveAndFlush(u);

        if (status == UserStatus.BLOCKED) {
            refreshTokenRepository.revokeAllByUserId(u.getId());
        }
    }

    @Transactional
    public UserDto changeRoles(Long id, Set<UserRole> roles) {
        var u = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        u.setRoles(roles == null || roles.isEmpty() ? EnumSet.of(UserRole.KAMERAL) : EnumSet.copyOf(roles));
        repo.saveAndFlush(u);
        return toDto(u);
    }

    @Transactional
    public void delete(Long id) {
        var u = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        repo.softDeleteById(u.getId());
        refreshTokenRepository.revokeAllByUserId(u.getId());
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        var u = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (newPassword == null || newPassword.trim().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        u.setPasswordHash(passwordEncoder.encode(newPassword.trim()));
        u.setUpdatedAt(OffsetDateTime.now());
        repo.saveAndFlush(u);
        refreshTokenRepository.revokeAllByUserId(u.getId());
    }

    @Transactional
    public void changePasswordBySubject(String sub, String newPassword) {
        var user = parseAsLong(sub).flatMap(repo::findById)
                .or(() -> repo.findByEmailIgnoreCase(sub))
                .orElseThrow(() -> new IllegalArgumentException("User not found by subject"));
        if (newPassword == null || newPassword.trim().length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
        user.setPasswordHash(passwordEncoder.encode(newPassword.trim()));
        user.setUpdatedAt(OffsetDateTime.now());
        repo.saveAndFlush(user);
    }

    @Transactional
    public UserDto register(RegisterDto dto) {
        if (repo.existsByEmailIgnoreCase(dto.email())) throw new IllegalArgumentException("Email already in use");
        if (repo.existsByUsernameIgnoreCase(dto.username())) throw new IllegalArgumentException("Username already in use");
        String phone = requireE164(dto.phone());
        if (repo.existsByPhone(phone)) throw new IllegalArgumentException("Phone already in use");

        var u = new AppUser();
        u.setFullName(dto.fullName().trim());
        u.setPhone(phone);
        u.setEmail(dto.email().trim());
        u.setUsername(dto.username().trim());
        u.setPasswordHash(passwordEncoder.encode(dto.password()));
        u.setStatus(UserStatus.ACTIVE);
        u.setDateOfBirth(dto.dateOfBirth());
        u.setRoles(EnumSet.of(UserRole.KAMERAL));

        return toDto(repo.save(u));
    }

    public Page<UserDto> list(UserFilter filter, Pageable pageable) {
        return repo.findAll(UserSpecs.byFilter(filter), pageable)
                .map(UserService::toDto);
    }
}