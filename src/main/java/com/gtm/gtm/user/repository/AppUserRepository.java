package com.gtm.gtm.user.repository;

import com.gtm.gtm.user.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByPhone(String phone); // E.164
    Optional<AppUser> findByEmailIgnoreCase(String email);
}
