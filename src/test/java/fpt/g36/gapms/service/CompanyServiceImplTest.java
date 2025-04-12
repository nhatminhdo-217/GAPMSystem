package fpt.g36.gapms.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import fpt.g36.gapms.models.dto.CompanyDTO;
import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.CompanyRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.impls.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private User testUser;
    private CompanyDTO companyDTO;
    private Company mockCompany_fiId;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");


        companyDTO = new CompanyDTO();
        companyDTO.setName("Test Company");
        companyDTO.setEmail("testcompany@gmail.com");
        companyDTO.setPhoneNumber("0933666888");
        companyDTO.setAddress("123 Test Street");
        companyDTO.setTaxNumber("TAX12345");

        mockCompany_fiId = new Company();
        mockCompany_fiId.setId(1L);
        mockCompany_fiId.setName("Test Company");
    }

    @Test
    void findByUserId_UserExists_ReturnsCompany() {
        // Giả lập việc tìm thấy công ty cho userId
        Long userId = 1L;
        when(companyRepository.findCompanyByUserId(userId)).thenReturn(Optional.of(mockCompany_fiId));

        // Gọi phương thức cần kiểm thử
        Optional<Company> result = companyService.findByUserId(userId);

        // Kiểm tra kết quả
        assertTrue(result.isPresent(), "Công ty không được trả về");
        assertEquals(1L, result.get().getId(), "ID công ty không đúng");
        assertEquals("Test Company", result.get().getName(), "Tên công ty không đúng");

        // Kiểm tra phương thức được gọi đúng một lần
        verify(companyRepository, times(1)).findCompanyByUserId(userId);
    }

    @Test
    void findByUserId_UserNotExists_ReturnsEmpty() {
        // Giả lập không tìm thấy công ty cho userId
        Long userId = 1L;
        when(companyRepository.findCompanyByUserId(userId)).thenReturn(Optional.empty());

        // Gọi phương thức cần kiểm thử
        Optional<Company> result = companyService.findByUserId(userId);

        // Kiểm tra kết quả
        assertFalse(result.isPresent(), "Kết quả phải là Optional.empty() khi không tìm thấy công ty");

        // Kiểm tra phương thức được gọi đúng một lần
        verify(companyRepository, times(1)).findCompanyByUserId(userId);
    }


    @Test
    void addNewCompany() {
        // Giả lập việc tìm thấy user
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Giả lập việc lưu company
        Company mockCompany = new Company();
        mockCompany.setId(1L);
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompany);

        // Gọi phương thức cần kiểm thử
        Company result = companyService.addCompany(1L, companyDTO);

        // Kiểm tra kết quả
        assertNotNull(result, "Công ty không được trả về null");
        assertEquals(1L, result.getId(), "ID công ty không đúng");

        // Kiểm tra các phương thức được gọi
        verify(userRepository, times(1)).findById(1L);
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void addCompany_UserNotFound_ThrowsException() {
        // Giả lập không tìm thấy user
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ RuntimeException khi không tìm thấy user
        RuntimeException exception = assertThrows(RuntimeException.class, () -> companyService.addCompany(1L, companyDTO));
        assertEquals("User not found", exception.getMessage());

        // Kiểm tra phương thức không được gọi
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void updateCompany() {
        // Giả lập việc tìm thấy công ty theo userId
        Company mockCompany = new Company();
        mockCompany.setId(1L);
        mockCompany.setEmail("hailamgno28@gmail.com");
        when(companyRepository.findCompanyByUserId(1L)).thenReturn(Optional.of(mockCompany));

        // Giả lập việc lưu công ty
        Company updatedCompany = new Company();
        updatedCompany.setId(1L);
        updatedCompany.setEmail("testcompany@example.com");
        when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);

        // Gọi phương thức cần kiểm thử
        Company result = companyService.updateCompany(1L, companyDTO);

        // Kiểm tra kết quả
        assertNotNull(result, "Công ty không được trả về null");
        assertEquals("testcompany@example.com", result.getEmail());
        assertNotEquals("longaacc@gmail.com", result.getEmail());
        assertEquals(1L, result.getId(), "ID công ty không đúng");

        // Kiểm tra các phương thức được gọi
        verify(companyRepository, times(1)).findCompanyByUserId(1L);
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void updateCompany_NoCompanyFound_ThrowsException() {

        when(companyRepository.findCompanyByUserId(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> companyService.updateCompany(1L, companyDTO));
        assertEquals("No company found for this user.", exception.getMessage());

        verify(companyRepository, times(1)).findCompanyByUserId(1L);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void getCompanyByUserId() {

        Company mockCompany = new Company();
        mockCompany.setId(1L);
        when(companyRepository.getCompanyByUserId(1L)).thenReturn(mockCompany);


        Company result = companyService.getCompanyByUserId(1L);

        assertNotNull(result, "Công ty không được trả về null");
        assertEquals(1L, result.getId(), "Không tìm thấy công ty nào cho người dùng này");

        verify(companyRepository, times(1)).getCompanyByUserId(1L);
    }

    @Test
    void autoGenCompany() {
        Long userId = 1L;

        // Mock user
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("Sale staff");
        mockUser.setEmail("hailamngo28@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Mock save: gán ID để không bị null
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company company = invocation.getArgument(0);
            company.setId(userId); // Giả sử ID trùng userId để dễ kiểm tra
            return company;
        });

        // Tạo DTO
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setName("AutoGen Company for Sale staff");
        companyDTO.setEmail("hailamngo28@gmail.com");
        companyDTO.setPhoneNumber("0243 552 0488");
        companyDTO.setAddress("Xã Tân Triều, Thanh Trì, Hà Nội");
        companyDTO.setTaxNumber("01011965991");

        // Gọi service
        Company result = companyService.addCompany(userId, companyDTO);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId()); // ID không còn null
        assertEquals(companyDTO.getName(), result.getName());
        assertEquals(companyDTO.getEmail(), result.getEmail());
        assertEquals(companyDTO.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(companyDTO.getAddress(), result.getAddress());
        assertEquals(companyDTO.getTaxNumber(), result.getTaxNumber());
        assertEquals(1, result.getUsers().size());
        assertTrue(result.getUsers().contains(mockUser));

        // Verify
        verify(userRepository, times(1)).findById(userId);
        verify(companyRepository, times(1)).save(any(Company.class));
    }




}
