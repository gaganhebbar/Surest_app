package com.devassignment.demo.config;

import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin_user");
    }

    @Test
    void testGenerateTokenNotNull() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token, "Token must not be null");
    }


    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken(userDetails);
        String extracted = jwtUtil.extractUsername(token);
        assertEquals("admin_user", extracted);
    }


    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken(userDetails);
        boolean isValid = jwtUtil.validateToken(token, userDetails);
        assertTrue(isValid, "Token should be valid");
    }


    @Test
    void testValidateTokenWithWrongUsername() {
        String token = jwtUtil.generateToken(userDetails);

        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("differentUser");

        assertFalse(jwtUtil.validateToken(token, otherUser));
    }


    @Test
    void testIsTokenExpired() throws InterruptedException {
        JwtUtil shortLivedJwt = new JwtUtil() {
            @Override
            public String generateToken(UserDetails userDetails) {
                // Generate token that expires in 1 ms
                return io.jsonwebtoken.Jwts.builder()
                        .setSubject(userDetails.getUsername())
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + 1))
                        .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                        .compact();
            }
        };

        String token = shortLivedJwt.generateToken(userDetails);

        Thread.sleep(5); // Allow expiration

        assertThrows(ExpiredJwtException.class, () -> {
            shortLivedJwt.validateToken(token, userDetails);
        });
    }


    @Test
    void testTokenWithExpiredToken() {
        JwtUtil shortLivedJwt = new JwtUtil() {
            @Override
            public String generateToken(UserDetails userDetails) {
                return io.jsonwebtoken.Jwts.builder()
                        .setSubject(userDetails.getUsername())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() - 1000)) // already expired
                        .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                        .compact();
            }
        };

        String expiredToken = shortLivedJwt.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> {
            shortLivedJwt.validateToken(expiredToken, userDetails);
        });
    }

}
