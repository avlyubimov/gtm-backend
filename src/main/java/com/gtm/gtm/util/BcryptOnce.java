package com.gtm.gtm.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptOnce {
    public static void main(String[] args) {
        var enc = new BCryptPasswordEncoder(12);
        System.out.println(enc.encode("Admin123!"));
    }
}
