package com.gtm.gtm.auth.service;

import com.gtm.gtm.auth.domain.AuthAudit;
import com.gtm.gtm.auth.repository.AuthAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service
@RequiredArgsConstructor
public class AuthAuditService {
    private final AuthAuditRepository repo;

    public void success(Long userId, String login, InetAddress ip, String ua) {
        var a = new AuthAudit();
        a.setUserId(userId);
        a.setLogin(login);
        a.setIp(ip);
        a.setUserAgent(ua);
        a.setSuccess(true);
        repo.save(a);
    }
    public void failure(String login, InetAddress ip, String ua, String error) {
        var a = new AuthAudit();
        a.setLogin(login);
        a.setIp(ip);
        a.setUserAgent(ua);
        a.setSuccess(false);
        a.setError(error);
        repo.save(a);
    }
}
