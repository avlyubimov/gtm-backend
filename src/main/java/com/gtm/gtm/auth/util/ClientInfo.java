package com.gtm.gtm.auth.util;

import jakarta.servlet.http.HttpServletRequest;

public class ClientInfo {
    public static String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String realIp = req.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) return realIp.trim();
        return req.getRemoteAddr();
    }

    public static String deviceFromUA(String ua) {
        if (ua == null) return "Unknown";
        String u = ua.toLowerCase();
        String os =
                u.contains("iphone") || u.contains("ios") ? "iOS" :
                        u.contains("android") ? "Android" :
                                u.contains("windows") ? "Windows" :
                                        u.contains("mac os x") || u.contains("macintosh") ? "macOS" :
                                                u.contains("linux") ? "Linux" : "Other";

        String browser =
                u.contains("edg/") || u.contains("edg ") ? "Edge" :
                        u.contains("chrome/") && !u.contains("chromium") ? "Chrome" :
                                u.contains("safari/") && !u.contains("chrome/") ? "Safari" :
                                        u.contains("firefox/") ? "Firefox" :
                                                u.contains("opera") || u.contains("opr/") ? "Opera" : "Browser";

        return browser + " on " + os;
    }
}
