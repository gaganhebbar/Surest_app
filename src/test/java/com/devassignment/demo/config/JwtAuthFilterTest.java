package com.devassignment.demo.config;

import com.devassignment.demo.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthFilterTest {

    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        jwtAuthFilter = new JwtAuthFilter();  // your real class with @Autowired fields

        // Inject mocks into private @Autowired fields
        ReflectionTestUtils.setField(jwtAuthFilter, "jwtService", jwtUtil);
        ReflectionTestUtils.setField(jwtAuthFilter, "userDetailsService", userDetailsService);
    }

    @Test
    void testSkipLoginPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testNoAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        request.addHeader("Authorization", "Bearer valid.token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin_user");
        when(userDetails.getAuthorities()).thenReturn(null);

        when(jwtUtil.extractUsername("valid.token")).thenReturn("admin_user");
        when(userDetailsService.loadUserByUsername("admin_user")).thenReturn(userDetails);
        when(jwtUtil.validateToken("valid.token", userDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertNotNull(
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }
}
