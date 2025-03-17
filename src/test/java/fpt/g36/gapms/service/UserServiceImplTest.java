package fpt.g36.gapms.service;

import fpt.g36.gapms.models.dto.UpdateProfileDTO;
import fpt.g36.gapms.models.dto.UserDTO;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.impls.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
    void registerUser_Success() {
        // Mock dữ liệu đầu vào
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("Test@1234");
        userDTO.setEmail("test@example.com");
        userDTO.setPhoneNumber("0123456789");

        // Mock vai trò "CUSTOMER" có tồn tại
        Role userRole = new Role();
        userRole.setName("CUSTOMER");

        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Gọi phương thức cần kiểm thử
        User result = userService.register(userDTO);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("hashedpassword", result.getPassword());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("0123456789", result.getPhoneNumber());
        assertEquals("default-avatar.png", result.getAvatar());
        assertFalse(result.isVerified());
        assertTrue(result.isActive());
        assertEquals(userRole, result.getRole());

        // Kiểm tra repository được gọi đúng cách
        verify(roleRepository, times(1)).findByName("CUSTOMER");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_RoleNotFound_ThrowsException() {
        // Mock dữ liệu đầu vào
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("Test@1234");
        userDTO.setEmail("test@example.com");
        userDTO.setPhoneNumber("0123456789");

        // Vai trò không tồn tại
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(userDTO));
        assertEquals("Error: Role USER is not found.", exception.getMessage());

        // Kiểm tra repository không lưu User
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void UserServiceImpl_findByEmailOrPhone_foundByEmail() {
        // Arrange
        when(userRepository.findByEmailOrPhoneNumber("longaaccbb1605@gmail.com", "987654321")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmailOrPhone("longaaccbb1605@gmail.com", "987654321");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("longaaccbb1605@gmail.com", result.get().getEmail());
    }

    @Test
    void UserServiceImpl_findByEmailOrPhone_foundByPhone() {
        // Arrange
        when(userRepository.findByEmailOrPhoneNumber("test@example.com", "0969792482")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmailOrPhone("test@example.com", "0969792482");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("0969792482", result.get().getPhoneNumber());
    }

    @Test
    void UserServiceImpl_findByEmailOrPhone_notFound() {

        // Arrange
        when(userRepository.findByEmailOrPhoneNumber("test@example.com", "987654321")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmailOrPhone("test@example.com", "987654321");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void checkPassword_CorrectPassword_ReturnsTrue() {
        // Mock dữ liệu đầu vào
        String rawPassword = "Test@1234";
        String hashedPassword = "$2a$10$somethinghashed";

        // Giả lập passwordEncoder.matches trả về true
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        // Gọi phương thức kiểm thử
        boolean result = userService.checkPassword(rawPassword, hashedPassword);

        // Kiểm tra kết quả
        assertTrue(result);

        // Kiểm tra xem phương thức matches có được gọi hay không
        verify(passwordEncoder, times(1)).matches(rawPassword, hashedPassword);
    }

    @Test
    void checkPassword_WrongPassword_ReturnsFalse() {
        // Mock dữ liệu đầu vào
        String rawPassword = "WrongPassword";
        String hashedPassword = "$2a$10$somethinghashed";

        // Giả lập passwordEncoder.matches trả về false
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        // Gọi phương thức kiểm thử
        boolean result = userService.checkPassword(rawPassword, hashedPassword);

        // Kiểm tra kết quả
        assertFalse(result);

        // Kiểm tra xem phương thức matches có được gọi hay không
        verify(passwordEncoder, times(1)).matches(rawPassword, hashedPassword);
    }


    @Test
    void updatePassword_Success() {
        // Giả lập người dùng cần cập nhật mật khẩu
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("oldHashedPassword");

        // Mật khẩu mới
        String newPassword = "New@Password123";
        String newHashedPassword = "$2a$10$newhashedpassword"; // Giả lập mật khẩu mới đã mã hóa

        // Giả lập passwordEncoder.encode trả về mật khẩu đã mã hóa
        when(passwordEncoder.encode(newPassword)).thenReturn(newHashedPassword);

        // Gọi phương thức cần kiểm thử
        userService.updatePassword(user, newPassword);

        // Kiểm tra xem mật khẩu của user đã được cập nhật đúng không
        assertEquals(newHashedPassword, user.getPassword(), "Mật khẩu mới không được cập nhật đúng");

        // Kiểm tra repository đã được gọi để lưu user
        verify(userRepository, times(1)).save(user);
    }


    @Test
    @Transactional
    void updateUser_Success() {
        // Giả lập user có sẵn trong DB

        Role role = new Role();
        role.setName("ADMIN");

        Role role_dto = new Role();
        role_dto.setName("SALE");

        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setRole(role);
        existingUser.setActive(false);

        // Giả lập dữ liệu cập nhật
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(role_dto);
        userDTO.setActive(true);

        // Giả lập userRepository.findById trả về user
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Gọi phương thức cần kiểm thử
        String result = userService.updateUser(userId, userDTO);

        // Kiểm tra user đã được cập nhật đúng không
        assertEquals("SALE", existingUser.getRole().getName());
        assertTrue(existingUser.isActive());
        assertEquals("User updated successfully", result);
        // Kiểm tra userRepository.save được gọi 1 lần
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // Giả lập user không tồn tại
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ RuntimeException khi user không tồn tại
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userId, userDTO));
        assertEquals("User not found", exception.getMessage());

        // Kiểm tra userRepository.save không được gọi
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updatePersonalUser_Success() {
        // Giả lập userId
        Long userId = 1L;

        // Giả lập người dùng cũ trong DB
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");
        existingUser.setEmail("old@example.com");
        existingUser.setPhoneNumber("0123456789");
        existingUser.setAvatar("old-avatar.png");

        // Giả lập dữ liệu mới cần cập nhật
        UpdateProfileDTO userDTO = new UpdateProfileDTO();
        userDTO.setUsername("newUsername");
        userDTO.setEmail("new@example.com");
        userDTO.setPhoneNumber("0987654321");
        userDTO.setAvatar("new-avatar.png");

        // Giả lập userRepository.findById trả về user
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Gọi phương thức cần kiểm thử
        String result = userService.updatePersonalUser(userId, userDTO);

        // Kiểm tra user đã được cập nhật đúng không
        assertEquals("newUsername", existingUser.getUsername());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("0987654321", existingUser.getPhoneNumber());
        assertEquals("new-avatar.png", existingUser.getAvatar());
        assertEquals("User updated successfully", result);

        // Kiểm tra userRepository.save được gọi đúng một lần
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updatePersonalUser_UserNotFound_ThrowsException() {
        // Giả lập user không tồn tại
        Long userId = 1L;
        UpdateProfileDTO userDTO = new UpdateProfileDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ RuntimeException khi user không tồn tại
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updatePersonalUser(userId, userDTO));
        assertEquals("User not found", exception.getMessage());

        // Kiểm tra userRepository.save không được gọi
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @Transactional
    void updateUserStatus_Success() {
        // Giả lập userId
        Long userId = 1L;

        // Giả lập người dùng cũ trong DB
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setActive(false);

        // Giả lập userRepository.findById trả về user
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Gọi phương thức cần kiểm thử để cập nhật trạng thái user thành active
        userService.updateUserStatus(userId, true);

        // Kiểm tra user đã được cập nhật đúng không
        assertEquals(true, existingUser.isActive());

        // Kiểm tra userRepository.save được gọi đúng một lần
        verify(userRepository, times(1)).save(existingUser);
    }
    @Test
    @Transactional
    void updateUserStatus_UserNotFound_ThrowsException() {
        // Giả lập user không tồn tại
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ RuntimeException khi user không tồn tại
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUserStatus(userId, true));
        assertEquals("User not found", exception.getMessage());

        // Kiểm tra userRepository.save không được gọi
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        // Giả lập userId
        Long userId = 1L;

        // Giả lập một user tồn tại trong DB
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setUsername("testuser");

        // Giả lập userRepository.findById trả về user
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Gọi phương thức cần kiểm thử
        User result = userService.getUserById(userId);

        // Kiểm tra kết quả
        assertNotNull(result );
        assertEquals(userId, result.getId() );
        assertEquals("testuser", result.getUsername());

        // Kiểm tra userRepository.findById được gọi đúng một lần
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        // Giả lập user không tồn tại
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ RuntimeException khi user không tồn tại
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
        assertEquals("User not found", exception.getMessage());

        // Kiểm tra userRepository.findById được gọi đúng một lần
        verify(userRepository, times(1)).findById(userId);
    }


    @Test
    void findUsersByRfqId_UserExists_ReturnsUser() {
        // Giả lập rfqId
        long rfqId = 1001L;

        // Giả lập một user có rfqId tồn tại trong DB
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("rfqUser");

        // Giả lập userRepository.findUsersByRfqId trả về Optional<User>
        when(userRepository.findUsersByRfqId(rfqId)).thenReturn(Optional.of(expectedUser));

        // Gọi phương thức cần kiểm thử
        Optional<User> result = userService.findUsersByRfqId(rfqId);

        // Kiểm tra kết quả
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("rfqUser", result.get().getUsername());

        // Kiểm tra userRepository.findUsersByRfqId được gọi đúng một lần
        verify(userRepository, times(1)).findUsersByRfqId(rfqId);
    }

    @Test
    void findUsersByRfqId_UserNotFound_ReturnsEmptyOptional() {
        // Giả lập rfqId không có user
        long rfqId = 1001L;

        when(userRepository.findUsersByRfqId(rfqId)).thenReturn(Optional.empty());

        // Gọi phương thức cần kiểm thử
        Optional<User> result = userService.findUsersByRfqId(rfqId);

        // Kiểm tra kết quả
        assertFalse(result.isPresent());

        // Kiểm tra userRepository.findUsersByRfqId được gọi đúng một lần
        verify(userRepository, times(1)).findUsersByRfqId(rfqId);
    }
    @AfterAll
    static void afterAll() {
        System.out.println("UserServiceImplTest");
    }
}
