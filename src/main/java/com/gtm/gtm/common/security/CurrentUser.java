package com.gtm.gtm.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public Long id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwt) {
            Object sub = jwt.getTokenAttributes().get("sub");
            if (sub != null) {
                try { return Long.valueOf(sub.toString()); } catch (Exception ignored) { }
            }
            Object uid = jwt.getTokenAttributes().get("uid");
            if (uid != null) {
                try { return Long.valueOf(uid.toString()); } catch (Exception ignored) { }
            }
        }
        try { return Long.valueOf(auth.getName()); } catch (Exception e) {
            throw new IllegalStateException("Can't determine current user id");
        }
    }
}
