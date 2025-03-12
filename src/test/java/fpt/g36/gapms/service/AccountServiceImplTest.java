package fpt.g36.gapms.service;


import fpt.g36.gapms.models.dto.CreateAccountDTO;
import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.CompanyUser;
import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.CompanyRepository;
import fpt.g36.gapms.repositories.CompanyUserRepository;
import fpt.g36.gapms.repositories.RoleRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.impls.AccountServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CompanyUserRepository companyUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeAll
    static void beforeAll() {
        System.out.println("AccountServiceImplTest");
    }
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Thiết lập mock cho SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    void getUserById_success(){
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("long");
        user.setPhoneNumber("0123456789");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = accountService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("long", result.getUsername());
        assertEquals("0123456789", result.getPhoneNumber());
    }

    @Test
    void getUserById_notFound(){
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = accountService.getUserById(userId);

        assertNull(result);
    }

    @Test
    void searchAccountsWithoutPaging_Success() {
        // Giả lập email của người dùng hiện tại
        String currentEmail = "currentuser@example.com";
        String keyword = "searchKeyword";

        // Giả lập người dùng hiện tại
        User currentUser = new User();
        currentUser.setEmail(currentEmail);
        currentUser.setPhoneNumber("0123456789");

        // Giả lập danh sách user kết quả
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPhoneNumber("0987654321");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPhoneNumber("0976543210");

        List<User> expectedUsers = List.of(user1, user2);

        // Giả lập SecurityContext để lấy email của người dùng hiện tại
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentEmail);

        // Giả lập tìm user hiện tại trong database
        when(userRepository.findByEmailOrPhoneNumber(currentEmail, currentEmail)).thenReturn(Optional.of(currentUser));

        // Giả lập tìm kiếm user với từ khóa, loại trừ user hiện tại
        when(userRepository.findByKeywordExcludingCurrentUserEmail(keyword, currentUser.getEmail(), currentUser.getPhoneNumber()))
                .thenReturn(expectedUsers);

        // Gọi phương thức cần kiểm thử
        List<User> result = accountService.searchAccountsWithoutPaging(keyword);

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách user không được trả về null");
        assertEquals(2, result.size(), "Số lượng user trả về không đúng");
        assertEquals("user1@example.com", result.get(0).getEmail(), "Email user đầu tiên không đúng");
        assertEquals("user2@example.com", result.get(1).getEmail(), "Email user thứ hai không đúng");

        // Kiểm tra các phương thức mock được gọi đúng số lần
        verify(userRepository, times(1)).findByEmailOrPhoneNumber(currentEmail, currentEmail);
        verify(userRepository, times(1)).findByKeywordExcludingCurrentUserEmail(keyword, currentUser.getEmail(), currentUser.getPhoneNumber());
    }


    @Test
    void getAllAccountExcept_Success() {
        // Giả lập email của người dùng hiện tại
        String currentEmail = "currentuser@example.com";


        // Giả lập người dùng hiện tại
        User currentUser = new User();
        currentUser.setEmail(currentEmail);
        currentUser.setPhoneNumber("0123456789");

        // Giả lập danh sách user kết quả
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPhoneNumber("0987654321");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPhoneNumber("0976543210");

        List<User> expectedUsers = List.of(user1, user2);

        // Giả lập SecurityContext để lấy email của người dùng hiện tại
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentEmail);

        // Giả lập tìm user hiện tại trong database
        when(userRepository.findByEmailOrPhoneNumber(currentEmail, currentEmail)).thenReturn(Optional.of(currentUser));

        // Giả lập tìm kiếm user với từ khóa, loại trừ user hiện tại
        when(userRepository.findAllByEmailNotAndPhoneNumberNot( currentUser.getEmail(), currentUser.getPhoneNumber()))
                .thenReturn(expectedUsers);

        // Gọi phương thức cần kiểm thử
        List<User> result = accountService.getAllAccountExcept();

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1@example.com", result.get(0).getEmail());
        assertEquals("user2@example.com", result.get(1).getEmail());

        // Kiểm tra các phương thức mock được gọi đúng số lần
        verify(userRepository, times(1)).findByEmailOrPhoneNumber(currentEmail, currentEmail);
        verify(userRepository, times(1)).findAllByEmailNotAndPhoneNumberNot(currentUser.getEmail(), currentUser.getPhoneNumber());
    }



    @Test
    void createAccount_Success_CustomerRole() {
        // Giả lập CreateAccountDTO đầu vào
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setUsername("newuser");
        createAccountDTO.setEmail("newuser@example.com");
        createAccountDTO.setPhoneNumber("0123456789");

        Role role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");
        createAccountDTO.setRole(role);

        String rawPassword = "Secure@123";
        String encodedPassword = "encodedSecure@123";

        // Giả lập kiểm tra email và số điện thoại không tồn tại
        when(userRepository.existsByEmail(createAccountDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(createAccountDTO.getPhoneNumber())).thenReturn(false);

        // Giả lập mã hóa mật khẩu
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // Giả lập tìm thấy role hợp lệ
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // Giả lập lưu user
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Gọi phương thức cần kiểm thử
        User result = accountService.createAccount(createAccountDTO, rawPassword);

        // Kiểm tra kết quả
        assertNotNull(result, "User không được tạo");
        assertEquals("newuser", result.getUsername(), "Username không đúng");
        assertEquals("newuser@example.com", result.getEmail(), "Email không đúng");
        assertEquals("0123456789", result.getPhoneNumber(), "Phone number không đúng");
        assertEquals("default-avatar.png", result.getAvatar(), "Avatar không đúng");
        assertEquals(encodedPassword, result.getPassword(), "Mật khẩu không được mã hóa đúng");
        assertTrue(result.isVerified(), "User phải được xác thực");
        assertTrue(result.isActive(), "User phải active");
        assertEquals("CUSTOMER", result.getRole().getName(), "Vai trò không đúng");

        // Kiểm tra các phương thức mock được gọi đúng số lần
        verify(userRepository, times(1)).existsByEmail(createAccountDTO.getEmail());
        verify(userRepository, times(1)).existsByPhoneNumber(createAccountDTO.getPhoneNumber());
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
        verify(companyUserRepository, never()).save(any(CompanyUser.class)); // Không lưu CompanyUser khi role là CUSTOMER
    }


    @Test
    void createAccount_Success_NonCustomerRole() {
        // Giả lập CreateAccountDTO đầu vào
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setUsername("adminuser");
        createAccountDTO.setEmail("admin@example.com");
        createAccountDTO.setPhoneNumber("0987654321");

        Role role = new Role();
        role.setId(2L);
        role.setName("ADMIN");
        createAccountDTO.setRole(role);

        String rawPassword = "Secure@123";
        String encodedPassword = "encodedSecure@123";

        // Giả lập kiểm tra email và số điện thoại không tồn tại
        when(userRepository.existsByEmail(createAccountDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(createAccountDTO.getPhoneNumber())).thenReturn(false);

        // Giả lập mã hóa mật khẩu
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // Giả lập tìm thấy role hợp lệ
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));

        // Giả lập lưu user
        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setUsername(createAccountDTO.getUsername());
        savedUser.setEmail(createAccountDTO.getEmail());
        savedUser.setPhoneNumber(createAccountDTO.getPhoneNumber());
        savedUser.setPassword(encodedPassword);
        savedUser.setRole(role);
        savedUser.setVerified(true);
        savedUser.setActive(true);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Giả lập tìm công ty
        Company company = new Company();
        company.setId(5L);
        when(companyRepository.getCompanyByEmail("dntndungdong@gmail.com")).thenReturn(company);

        // Giả lập lưu CompanyUser
        when(companyUserRepository.save(any(CompanyUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Gọi phương thức cần kiểm thử
        User result = accountService.createAccount(createAccountDTO, rawPassword);

        // Kiểm tra kết quả
        assertNotNull(result, "User không được tạo");
        assertEquals("ADMIN", result.getRole().getName(), "Vai trò không đúng");
        assertEquals(10L, result.getId(), "User ID không đúng");


        // Kiểm tra CompanyUser đã được tạo
        verify(companyUserRepository, times(1)).save(any(CompanyUser.class));
    }

    @Test
    void createAccount_EmailExists_ThrowsException() {
        // Giả lập CreateAccountDTO đầu vào
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setEmail("existing@example.com");

        // Giả lập email đã tồn tại
        when(userRepository.existsByEmail(createAccountDTO.getEmail())).thenReturn(true);

        // Kiểm tra ngoại lệ RuntimeException khi email đã tồn tại
        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.createAccount(createAccountDTO, "password"));
        assertEquals("Email đã tồn tại!", exception.getMessage());

        // Kiểm tra userRepository.save không được gọi
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createAccount_PhoneNumberExists_ThrowsException() {
        // Giả lập CreateAccountDTO đầu vào
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setEmail("new@example.com");
        createAccountDTO.setPhoneNumber("0123456789");

        // Giả lập số điện thoại đã tồn tại
        when(userRepository.existsByEmail(createAccountDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(createAccountDTO.getPhoneNumber())).thenReturn(true);

        // Kiểm tra ngoại lệ RuntimeException khi số điện thoại đã tồn tại
        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.createAccount(createAccountDTO, "password"));

        assertEquals("Số điện thoại đã tồn tại!", exception.getMessage());

        // Kiểm tra userRepository.save không được gọi
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void existsByEmail_EmailExists_ReturnsTrue() {
        // Giả lập email đã tồn tại trong database
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Gọi phương thức cần kiểm thử
        boolean result = accountService.existsByEmail(email);

        // Kiểm tra kết quả
        assertTrue(result, "Phương thức phải trả về true khi email đã tồn tại");

        // Kiểm tra userRepository.existsByEmail được gọi đúng một lần
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void existsByEmail_EmailNotExists_ReturnsFalse() {
        // Giả lập email không tồn tại trong database
        String email = "new@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // Gọi phương thức cần kiểm thử
        boolean result = accountService.existsByEmail(email);

        // Kiểm tra kết quả
        assertFalse(result, "Phương thức phải trả về false khi email chưa tồn tại");

        // Kiểm tra userRepository.existsByEmail được gọi đúng một lần
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void existsByPhoneNumber_PhoneNumberExists_ReturnsTrue() {
        // Giả lập số điện thoại đã tồn tại trong database
        String phoneNumber = "0123456789";
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        // Gọi phương thức cần kiểm thử
        boolean result = accountService.existsByPhoneNumber(phoneNumber);

        // Kiểm tra kết quả
        assertTrue(result, "Phương thức phải trả về true khi số điện thoại đã tồn tại");

        // Kiểm tra userRepository.existsByPhoneNumber được gọi đúng một lần
        verify(userRepository, times(1)).existsByPhoneNumber(phoneNumber);
    }

    @Test
    void existsByPhoneNumber_PhoneNumberNotExists_ReturnsFalse() {
        // Giả lập số điện thoại không tồn tại trong database
        String phoneNumber = "0987654321";
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);

        // Gọi phương thức cần kiểm thử
        boolean result = accountService.existsByPhoneNumber(phoneNumber);

        // Kiểm tra kết quả
        assertFalse(result, "Phương thức phải trả về false khi số điện thoại chưa tồn tại");

        // Kiểm tra userRepository.existsByPhoneNumber được gọi đúng một lần
        verify(userRepository, times(1)).existsByPhoneNumber(phoneNumber);
    }
    @AfterAll
    static void afterAll() {
        System.out.println("AccountServiceImplTest");
    }
}
