package com.gtm.gtm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.auth")
public class SecurityAuthProperties {
    /**
     * Максимум активных refresh-сессий на пользователя.
     */
    private int maxSessionsPerUser = 5;

    public int getMaxSessionsPerUser() { return maxSessionsPerUser; }
    public void setMaxSessionsPerUser(int v) { this.maxSessionsPerUser = v; }
}
