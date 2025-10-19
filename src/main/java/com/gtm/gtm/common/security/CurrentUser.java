package com.gtm.gtm.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    /**
     * Возвращает ID текущего пользователя.
     * Берёт из JWT: сначала sub (если это число), затем uid (если присутствует).
     */
    public Long id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwt) {
            // sub как строка
            Object sub = jwt.getTokenAttributes().get("sub");
            if (sub != null) {
                try { return Long.valueOf(sub.toString()); } catch (Exception ignored) { }
            }
            // альтернативный claim uid
            Object uid = jwt.getTokenAttributes().get("uid");
            if (uid != null) {
                try { return Long.valueOf(uid.toString()); } catch (Exception ignored) { }
            }
        }
        // fallback на имя аутентификации
        try { return Long.valueOf(auth.getName()); } catch (Exception e) {
            throw new IllegalStateException("Can't determine current user id");
        }
    }
}
