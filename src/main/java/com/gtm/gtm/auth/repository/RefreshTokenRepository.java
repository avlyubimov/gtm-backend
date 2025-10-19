package com.gtm.gtm.auth.repository;

import com.gtm.gtm.auth.domain.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByJtiAndRevokedFalse(String jti);

    List<RefreshToken> findAllByUserIdAndRevokedFalseOrderByCreatedAtDesc(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshToken r set r.revoked = true where r.userId = :userId and r.revoked = false")
    void revokeAllByUserId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update RefreshToken r
           set r.revoked = true
         where r.revoked = false
           and r.expiresAt < CURRENT_TIMESTAMP
    """)
    int revokeExpired();

    @Modifying
    @Transactional
    @Query(value = """
        update gtm_refresh_token r
           set revoked = true
         where r.user_id = :userId
           and r.revoked = false
           and r.id not in (
             select id from gtm_refresh_token
              where user_id = :userId and revoked = false
              order by created_at desc
              limit :limit
           )
    """, nativeQuery = true)
    int enforceActiveLimit(@Param("userId") Long userId, @Param("limit") int limit);
}
