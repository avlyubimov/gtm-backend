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
        if (ua == null || ua.isBlank()) return "Unknown";
        String u = ua.toLowerCase();

        boolean isAndroid = u.contains("android");
        boolean isIOS = u.contains("iphone") || u.contains("ipad") || u.contains("ipod") || u.contains("ios");

        boolean hasOkHttp = u.contains("okhttp");
        boolean hasDalvik = u.contains("dalvik");
        boolean hasCFNetwork = u.contains("cfnetwork");
        boolean hasDarwin = u.contains("darwin");

        boolean hasChrome = u.contains("chrome/") || u.contains("crios/");
        boolean hasSafari = u.contains("safari/");
        boolean hasFirefox = u.contains("firefox/") || u.contains("fxios/");
        boolean hasEdge = u.contains("edg/") || u.contains("edg ");
        boolean hasOpera = u.contains("opera") || u.contains("opr/");

        boolean isWebViewAndroid = isAndroid && (u.contains("; wv") || u.contains(" wv)") || u.contains("wv)") || u.contains("version/"));
        boolean isWebViewIOS = isIOS && !hasSafari;
        boolean isWebView = isWebViewAndroid || isWebViewIOS;

        boolean looksLikeBrowser = hasChrome || hasSafari || hasFirefox || hasEdge || hasOpera;

        if (isAndroid && (hasOkHttp || hasDalvik || (!looksLikeBrowser) || isWebViewAndroid)) {
            return isWebViewAndroid ? "Android App (WebView)" : "Android App";
        }

        if (isIOS && (hasCFNetwork || hasDarwin || (!looksLikeBrowser) || isWebViewIOS)) {
            return isWebViewIOS ? "iOS App (WebView)" : "iOS App";
        }

        String os =
                isIOS ? "iOS" :
                isAndroid ? "Android" :
                (u.contains("windows") ? "Windows" :
                 (u.contains("mac os x") || u.contains("macintosh")) ? "macOS" :
                 (u.contains("linux") ? "Linux" : "Other"));

        String browser =
                hasEdge ? "Edge" :
                (hasChrome && !u.contains("chromium")) ? "Chrome" :
                (hasSafari && !hasChrome) ? "Safari" :
                hasFirefox ? "Firefox" :
                hasOpera ? "Opera" : "Browser";

        return browser + " on " + os;
    }
}
