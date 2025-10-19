package com.gtm.gtm.user.domain;

public enum UserRole {
    ADMIN, KAMERAL, MANAGER;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}
