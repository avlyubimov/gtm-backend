package com.gtm.gtm.user.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.user.domain.AppUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AppUserRepository
        extends SoftDeleteRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByPhone(String phone);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);

    Optional<AppUser> findByEmailIgnoreCase(String email);
    Optional<AppUser> findByUsernameIgnoreCase(String username);

    default Optional<AppUser> findByLogin(String login) {
        var byUsername = findByUsernameIgnoreCase(login);
        return byUsername.isPresent() ? byUsername : findByEmailIgnoreCase(login);
    }
}
