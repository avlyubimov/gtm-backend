package com.gtm.gtm.user.repository;

import com.gtm.gtm.user.domain.AppUser;
import com.gtm.gtm.user.domain.UserRole;
import com.gtm.gtm.user.domain.UserStatus;
import com.gtm.gtm.user.dto.UserFilter;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

public final class UserSpecs {

    private UserSpecs() {}

    public static Specification<AppUser> byFilter(UserFilter f) {
        return Specification
                .where(notDeleted())
                .and(q(f.q()))
                .and(eqIgnoreCase("email", f.email()))
                .and(eqIgnoreCase("username", f.username()))
                .and(eq("phone", f.phone()))
                .and(hasRoles(f.roles()))
                .and(hasStatus(f.status()))
                .and(hasPhoto(f.hasPhoto()))
                .and(betweenTs("createdAt", f.createdFrom(), f.createdTo()))
                .and(betweenTs("updatedAt", f.updatedFrom(), f.updatedTo()))
                .and(betweenTs("lastLoginAt", f.lastLoginFrom(), f.lastLoginTo()))
                .and(dobBetween(f.dobFrom(), f.dobTo()))
                .and(ageBetween(f.ageFrom(), f.ageTo()));
    }

    public static Specification<AppUser> notDeleted() {
        return (root, q, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<AppUser> q(String qv) {
        if (qv == null || qv.isBlank()) return null;
        String like = "%" + qv.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("fullName")), like),
                cb.like(cb.lower(root.get("email")), like),
                cb.like(cb.lower(root.get("username")), like),
                cb.like(cb.lower(root.get("phone")), like)
        );
    }

    public static Specification<AppUser> eqIgnoreCase(String field, String v) {
        if (v == null || v.isBlank()) return null;
        String val = v.trim().toLowerCase();
        return (root, q, cb) -> cb.equal(cb.lower(root.get(field)), val);
    }

    public static Specification<AppUser> eq(String field, String v) {
        if (v == null || v.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get(field), v.trim());
    }

    public static Specification<AppUser> hasStatus(UserStatus s) {
        if (s == null) return null;
        return (root, q, cb) -> cb.equal(root.get("status"), s);
    }

    public static Specification<AppUser> hasRoles(Set<UserRole> roles) {
        if (roles == null || roles.isEmpty()) return null;

        return (root, query, cb) -> {
            var join = root.joinSet("roles", JoinType.LEFT);
            assert query != null;
            query.distinct(true);
            return join.in(roles);
        };
    }

    public static Specification<AppUser> hasPhoto(Boolean hasPhoto) {
        if (hasPhoto == null) return null;
        return (root, q, cb) -> {

            var p = root.<String>get("photoUrl");
            return hasPhoto
                    ? cb.and(cb.isNotNull(p), cb.notEqual(p, ""))
                    : cb.or(cb.isNull(p), cb.equal(p, ""));
        };
    }

    public static Specification<AppUser> betweenTs(String field, OffsetDateTime from, OffsetDateTime to) {
        if (from == null && to == null) return null;
        return (root, q, cb) -> {
            if (from != null && to != null) return cb.between(root.get(field), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get(field), from);
            return cb.lessThanOrEqualTo(root.get(field), to);
        };
    }

    public static Specification<AppUser> dobBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;
        return (root, q, cb) -> {
            if (from != null && to != null) return cb.between(root.get("dateOfBirth"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("dateOfBirth"), from);
            return cb.lessThanOrEqualTo(root.get("dateOfBirth"), to);
        };
    }

    public static Specification<AppUser> ageBetween(Integer ageFrom, Integer ageTo) {
        if (ageFrom == null && ageTo == null) return null;

        LocalDate today = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate();
        LocalDate dobMin = null;
        LocalDate dobMax = null;

        if (ageTo != null) {
            dobMin = today.minusYears(ageTo + 1L).plusDays(1); // эксклюзивность исправляем +1 день
        }
        if (ageFrom != null) {
            dobMax = today.minusYears(ageFrom);
        }

        LocalDate finalDobMin = dobMin;
        LocalDate finalDobMax = dobMax;

        return (root, q, cb) -> {
            if (finalDobMin != null && finalDobMax != null) {
                return cb.between(root.get("dateOfBirth"), finalDobMin, finalDobMax);
            }
            if (finalDobMin != null) {
                return cb.greaterThanOrEqualTo(root.get("dateOfBirth"), finalDobMin);
            }
            return cb.lessThanOrEqualTo(root.get("dateOfBirth"), finalDobMax);
        };
    }
}
