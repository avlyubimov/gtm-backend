package com.gtm.gtm.user.service;

import com.gtm.gtm.user.domain.AppUser;
import com.gtm.gtm.user.domain.UserStatus;
import com.gtm.gtm.user.dto.UserCreateDto;
import com.gtm.gtm.user.dto.UserDto;
import com.gtm.gtm.user.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;

@Service
public class UserService {
    private final AppUserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    private static String requireE164(String phone) {
        var p = phone == null ? "" : phone.trim();
        if (!p.matches("^\\+\\d{7,15}$"))
            throw new IllegalArgumentException("Phone must be E.164, e.g. +77011234567");
        return p;
    }

    @Transactional
    public UserDto create(UserCreateDto dto) {
        if (repo.existsByEmailIgnoreCase(dto.email()))    throw new IllegalArgumentException("Email already in use");
        if (repo.existsByUsernameIgnoreCase(dto.username())) throw new IllegalArgumentException("Username already in use");

        String phone = requireE164(dto.phone());
        if (repo.existsByPhone(phone)) throw new IllegalArgumentException("Phone already in use");

        AppUser u = new AppUser();
        u.setFullName(dto.fullName().trim());
        u.setPhone(phone);
        u.setEmail(dto.email().trim());
        u.setUsername(dto.username().trim());
        u.setPasswordHash(passwordEncoder.encode(dto.password()));
        u.setStatus(UserStatus.ACTIVE);
        u.setDateOfBirth(dto.dateOfBirth());

        var roles = new LinkedHashSet<String>();
        if (dto.roles() != null && !dto.roles().isEmpty()) roles.addAll(dto.roles());
        else roles.add("USER");
        u.setRoles(roles);

        return toDto(repo.save(u));
    }

    public UserDto get(Long id) {
        return repo.findById(id).map(UserService::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private static UserDto toDto(AppUser u) {
        return new UserDto(
                u.getId(), u.getFullName(), u.getPhone(), u.getEmail(), u.getUsername(),
                u.getRoles(), u.getStatus(), u.getCreatedAt(), u.getUpdatedAt(), u.getLastLoginAt(),
                u.getDateOfBirth(), u.getAge()
        );
    }

    @Transactional
    public UserDto changePassword(Long id, String newPassword) {
        var u = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (newPassword == null || newPassword.trim().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        u.setPasswordHash(passwordEncoder.encode(newPassword.trim()));
        repo.saveAndFlush(u);
        return toDto(u);
    }
}
