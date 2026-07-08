package com.zerotrust.auth.service;

import com.zerotrust.auth.dto.request.LoginRequest;
import com.zerotrust.auth.dto.request.RegisterRequest;
import com.zerotrust.auth.dto.response.JwtAuthResponse;
import com.zerotrust.auth.entity.Permission;
import com.zerotrust.auth.entity.RefreshToken;
import com.zerotrust.auth.entity.Role;
import com.zerotrust.auth.entity.User;
import com.zerotrust.auth.exception.BadRequestException;
import com.zerotrust.auth.repository.RoleRepository;
import com.zerotrust.auth.repository.UserRepository;
import com.zerotrust.auth.security.CustomUserDetails;
import com.zerotrust.auth.security.jwt.JwtProvider;
import com.zerotrust.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private TokenService tokenService;
    @Mock
    private TokenBlacklistService tokenBlacklistService;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");
        userRole.setPermissions(Set.of(new Permission(1L, "READ_PROFILE")));

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(userRole);
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("new@example.com");
        request.setPassword("Password123!");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        authService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
        verify(auditService, times(1)).logAction(eq("REGISTER"), any(), eq("new@example.com"), any(), anyString());
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123!");

        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtProvider.generateAccessToken("test@example.com")).thenReturn("access-token-jwt");
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash("raw-refresh-token");
        when(tokenService.createRefreshToken(1L)).thenReturn(refreshToken);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        
        JwtAuthResponse response = authService.login(request, servletRequest);

        assertNotNull(response);
        assertEquals("access-token-jwt", response.getAccessToken());
        assertEquals("raw-refresh-token", response.getRefreshToken());
        assertEquals(1L, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }
}
