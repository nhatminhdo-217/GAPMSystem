package fpt.g36.gapms.service;

import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.impls.UserServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeAll
    static void beforeAll() {
        System.out.println("UserServiceImplTest");
    }

    private User user;

    // Thiết lập đối tượng User trước mỗi test case
    @BeforeEach
    void setUp() {
        // Tạo đối tượng User với các giá trị mặc định hoặc giá trị cần thiết cho từng test
        user = new User();
        user.setId(1L);
        user.setEmail("longaaccbb1605@gmail.com");
        user.setPhoneNumber("0969792482");
    }

    @Test
    void UserServiceImpl_findByEmailOrPhone_foundByEmail() {
        // Arrange
        Mockito.when(userRepository.findByEmailOrPhoneNumber("longaaccbb1605@gmail.com", "987654321")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmailOrPhone("longaaccbb1605@gmail.com", "987654321");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("longaaccbb1605@gmail.com", result.get().getEmail());
    }

    @Test
    void UserServiceImpl_findByEmailOrPhone_foundByPhone() {
        // Arrange
        Mockito.when(userRepository.findByEmailOrPhoneNumber("test@example.com", "0969792482")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmailOrPhone("test@example.com", "0969792482");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("0969792482", result.get().getPhoneNumber());
    }

    @Test
    void UserServiceImpl_findByEmailOrPhone_notFound() {

        // Arrange
        Mockito.when(userRepository.findByEmailOrPhoneNumber("test@example.com", "987654321")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmailOrPhone("test@example.com", "987654321");

        // Assert
        assertFalse(result.isPresent());
    }


    @AfterAll
    static void afterAll() {
        System.out.println("UserServiceImplTest");
    }
}
