package com.internship.tool.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String DUMMY_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // Must be Base64 and long enough

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Manually injecting the values that @Value would normally handle
        ReflectionTestUtils.setField(jwtUtil, "secretKey", DUMMY_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 3600000L); // 1 hour
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String username = "internUser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token, username));
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void shouldFailValidationForWrongUser() {
        String token = jwtUtil.generateToken("internUser");
        assertFalse(jwtUtil.isTokenValid(token, "wrongUser"));
    }
}