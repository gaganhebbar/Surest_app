package com.devassignment.demo.controller;

import com.devassignment.demo.config.JwtUtil;
import com.devassignment.demo.dto.LoginRequest;
import com.devassignment.demo.dto.LoginResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtService;

    @InjectMocks
    private AuthController authController;

    public AuthControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtUtil.class);

        authController = new AuthController(authenticationManager, jwtService);
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        LoginRequest req = new LoginRequest("admin_user", "AdminPass123");

        User user = new User("admin_user", "AdminPass123", List.of());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, List.of());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(any())).thenReturn("mock-jwt-token");

        // Act
        ResponseEntity<LoginResponse> response = authController.login(req);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("mock-jwt-token", response.getBody().getToken());
        assertEquals("admin_user", response.getBody().getUsername());
        assertEquals(200, response.getStatusCode().value());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(any());
    }

    @Test
    void testLoginFailureInvalidCredentials() {
        // Arrange
        LoginRequest req = new LoginRequest("wrong", "bad");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act + Assert
        assertThrows(RuntimeException.class, () -> authController.login(req));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
    }
}
