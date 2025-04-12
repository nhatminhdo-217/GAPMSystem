package fpt.g36.gapms.service;


import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.impls.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Chuẩn bị dữ liệu kiểm thử
        testRole = new Role();
        testRole.setName("CUSTOMER");

        testUser = new User();
        testUser.setPhoneNumber("1234567890");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$testpasswordhashed");
        testUser.setActive(true);
        testUser.setRole(testRole);
    }

    @Test
    void loadUserByUsername_Success_WithPhoneNumber() {
        // Sắp xếp: Giả lập phản hồi từ repository
        when(userRepository.findByEmailOrPhoneNumber(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));

        // Hành động: Gọi phương thức cần kiểm tra
        UserDetails userDetails = userDetailsService.loadUserByUsername("1234567890");

        // Kiểm tra: Xác minh kết quả
        assertNotNull(userDetails);
        assertEquals("1234567890", userDetails.getUsername());
        assertEquals("$2a$10$testpasswordhashed", userDetails.getPassword());
        assertEquals(testUser.isActive(), true);
        // Xác minh tương tác với mock
        verify(userRepository, times(1))
                .findByEmailOrPhoneNumber("1234567890", "1234567890");
    }

    @Test
    void loadUserByUsername_Success_WithEmail() {
        // Sắp xếp: Giả lập phản hồi từ repository
        when(userRepository.findByEmailOrPhoneNumber(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));

        // Hành động
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Kiểm tra
        assertNotNull(userDetails);
        assertEquals("1234567890", userDetails.getUsername());
        assertEquals("$2a$10$testpasswordhashed", userDetails.getPassword());
        assertEquals(testUser.isActive(), true);
        verify(userRepository, times(1))
                .findByEmailOrPhoneNumber("test@example.com", "test@example.com");
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Sắp xếp: Giả lập không tìm thấy người dùng
        when(userRepository.findByEmailOrPhoneNumber(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Hành động & Kiểm tra: Phải ném ngoại lệ UsernameNotFoundException
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown@example.com");
        }, "Phải ném ngoại lệ khi không tìm thấy người dùng");

        assertEquals("Tài khoản không tồn tại", exception.getMessage(),
                "Thông báo lỗi phải khớp");

        verify(userRepository, times(1))
                .findByEmailOrPhoneNumber("unknown@example.com", "unknown@example.com");
    }

    @Test
    void loadUserByUsername_DisabledAccount() {
        // Sắp xếp: Tạo người dùng bị khóa
        testUser.setActive(false);
        when(userRepository.findByEmailOrPhoneNumber(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));

        // Hành động & Kiểm tra: Phải ném ngoại lệ DisabledException
        DisabledException exception = assertThrows(DisabledException.class, () -> {
            userDetailsService.loadUserByUsername("1234567890");
        }, "Phải ném ngoại lệ khi tài khoản bị khóa");

        assertEquals("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.",
                exception.getMessage());

        verify(userRepository, times(1))
                .findByEmailOrPhoneNumber("1234567890", "1234567890");
    }
}
