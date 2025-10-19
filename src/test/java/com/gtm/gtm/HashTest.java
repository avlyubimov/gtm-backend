package com.gtm.gtm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class HashTest {
    @Autowired
    PasswordEncoder encoder;
    @Test
    void print() {
        System.out.println(encoder.encode("Admin123!"));
    }
}
