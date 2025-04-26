package fpt.g36.gapms.service;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.quotation.QuotationDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationDetailDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoDTO;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoProjection;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.models.mapper.QuotationMapper;
import fpt.g36.gapms.repositories.QuotationRepository;
import fpt.g36.gapms.services.RfqService;
import fpt.g36.gapms.services.impls.QuotationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotationServiceImplTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private QuotationMapper quotationMapper;

    @Mock
    private RfqService rfqService;

    @InjectMocks
    private QuotationServiceImpl quotationService;

    private Quotation quotation1;
    private Quotation quotation2;
    private QuotationDTO quotationDTO1;
    private QuotationDTO quotationDTO2;
    private List<Quotation> quotationList;
    private List<QuotationDTO> quotationDTOList;

    private Rfq rfqForQuotationInfo;
    private Quotation quotationForInfo;
    private RfqDetail rfqDetail1;
    private RfqDetail rfqDetail2;
    private QuotationInfoProjection mockProjection1;
    private QuotationInfoProjection mockProjection2;

    @BeforeEach
    void setUp() {
        // Create test data

        // Create users
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        // Create rfq
        Rfq rfq = new Rfq();
        rfq.setId(1L);
        rfq.setCreateBy(user);
        rfq.setExpectDeliveryDate(LocalDate.now().plusDays(30));

        // Create quotation1
        quotation1 = new Quotation();
        quotation1.setId(1L);
        quotation1.setRfq(rfq);
        quotation1.setIsCanceled(false);
        quotation1.setIsAccepted(BaseEnum.NOT_APPROVED);
        quotation1.setCreateAt(LocalDateTime.now());
        quotation1.setCreatedBy(user);

        // Create quotation2
        quotation2 = new Quotation();
        quotation2.setId(2L);
        quotation2.setRfq(rfq);
        quotation2.setIsCanceled(false);
        quotation2.setIsAccepted(BaseEnum.APPROVED);
        quotation2.setCreateAt(LocalDateTime.now().minusDays(1));
        quotation2.setCreatedBy(user);

        // List of quotations
        quotationList = Arrays.asList(quotation1, quotation2);

        // Create DTO objects
        quotationDTO1 = new QuotationDTO();
        quotationDTO1.setId(1L);
        quotationDTO1.setIsAccepted(BaseEnum.NOT_APPROVED);
        quotationDTO1.setCreateAt(quotation1.getCreateAt());
        quotationDTO1.setCustomerName("testUser");

        quotationDTO2 = new QuotationDTO();
        quotationDTO2.setId(2L);
        quotationDTO2.setIsAccepted(BaseEnum.APPROVED);
        quotationDTO2.setCreateAt(quotation2.getCreateAt());
        quotationDTO2.setCustomerName("testUser");

        // List of DTOs
        quotationDTOList = Arrays.asList(quotationDTO1, quotationDTO2);

        // Set up data for getQuotationInfo test
        // Create rfq detail
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");

        Brand brand1 = new Brand();
        brand1.setId(1L);
        brand1.setName("Brand 1");

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");

        Brand brand2 = new Brand();
        brand2.setId(2L);
        brand2.setName("Brand 2");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        rfqDetail1 = new RfqDetail();
        rfqDetail1.setId(1L);
        rfqDetail1.setProduct(product1);
        rfqDetail1.setBrand(brand1);
        rfqDetail1.setCate(category1);
        rfqDetail1.setQuantity(5);
        rfqDetail1.setNoteColor("#FFFFFF"); // White color = no color

        rfqDetail2 = new RfqDetail();
        rfqDetail2.setId(2L);
        rfqDetail2.setProduct(product2);
        rfqDetail2.setBrand(brand2);
        rfqDetail2.setCate(category2);
        rfqDetail2.setQuantity(10);
        rfqDetail2.setNoteColor("#FF0000"); // Red color = with color

        // Create Rfq for quotation info
        rfqForQuotationInfo = new Rfq();
        rfqForQuotationInfo.setId(3L);
        rfqForQuotationInfo.setCreateBy(user);
        rfqForQuotationInfo.setExpectDeliveryDate(LocalDate.now().plusDays(30));
        rfqForQuotationInfo.setRfqDetails(Arrays.asList(rfqDetail1, rfqDetail2));

        // Create quotation for info
        quotationForInfo = new Quotation();
        quotationForInfo.setId(3L);
        quotationForInfo.setRfq(rfqForQuotationInfo);
        quotationForInfo.setIsCanceled(false);
        quotationForInfo.setIsAccepted(BaseEnum.APPROVED);
        quotationForInfo.setCreatedBy(user);

        // Create mock projections
        mockProjection1 = new MockQuotationInfoProjection(
                3L, // getuotationId
                "testUser", // userName
                "Test Company", // companyName
                "1234567890", // taxNumber
                "123 Test St", // companyAddress
                BaseEnum.APPROVED, // getIsAccepted
                1L, // solutionId
                "Product 1", // productName
                "Brand 1", // brandName
                "Category 1", // categoryName
                5, // quantity
                new BigDecimal("100.00"), // price
                "#FFFFFF", // noteColor
                LocalDate.now().plusDays(30), // expectedDate
                LocalDate.now().plusDays(45) // actualDate
        );

        mockProjection2 = new MockQuotationInfoProjection(
                3L, // getuotationId
                "testUser", // userName
                "Test Company", // companyName
                "1234567890", // taxNumber
                "123 Test St", // companyAddress
                BaseEnum.APPROVED, // getIsAccepted
                1L, // solutionId
                "Product 2", // productName
                "Brand 2", // brandName
                "Category 2", // categoryName
                10, // quantity
                new BigDecimal("150.00"), // price
                "#FF0000", // noteColor
                LocalDate.now().plusDays(30), // expectedDate
                LocalDate.now().plusDays(45) // actualDate
        );
    }

    @Test
    @DisplayName("Should get all quotations with default parameters")
    void getAllQuotationWithDefaultParameters() {
        // Arrange
        String search = null;
        BaseEnum status = null;
        int page = 0;
        int size = 10;
        String sortField = "createAt";
        String sortDir = "desc";

        Pageable pageable = PageRequest.of(page, size);
        Page<Quotation> quotationPage = new PageImpl<>(quotationList, pageable, quotationList.size());

        when(quotationRepository.searchAndFilter(eq(search), eq(status), any(Pageable.class)))
                .thenReturn(quotationPage);
        when(quotationMapper.toListDTO(quotationPage)).thenReturn(quotationDTOList);

        // Act
        Page<QuotationDTO> result = quotationService.getAllQuotation(search, status, page, size, sortField, sortDir);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(quotationDTO1, result.getContent().get(0));
        assertEquals(quotationDTO2, result.getContent().get(1));

        verify(quotationRepository).searchAndFilter(eq(search), eq(status), any(Pageable.class));
        verify(quotationMapper).toListDTO(quotationPage);
    }

    @Test
    @DisplayName("Should get all quotations with search parameter")
    void getAllQuotationWithSearchParameter() {
        // Arrange
        String search = "test";
        BaseEnum status = null;
        int page = 0;
        int size = 10;
        String sortField = "createAt";
        String sortDir = "desc";

        Pageable pageable = PageRequest.of(page, size);
        Page<Quotation> quotationPage = new PageImpl<>(quotationList, pageable, quotationList.size());

        when(quotationRepository.searchAndFilter(eq(search), eq(status), any(Pageable.class)))
                .thenReturn(quotationPage);
        when(quotationMapper.toListDTO(quotationPage)).thenReturn(quotationDTOList);

        // Act
        Page<QuotationDTO> result = quotationService.getAllQuotation(search, status, page, size, sortField, sortDir);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(quotationDTO1, result.getContent().get(0));

        verify(quotationRepository).searchAndFilter(eq(search), eq(status), any(Pageable.class));
        verify(quotationMapper).toListDTO(quotationPage);
    }

    @Test
    @DisplayName("Should get all quotations with status filter")
    void getAllQuotationWithStatusFilter() {
        // Arrange
        String search = null;
        BaseEnum status = BaseEnum.APPROVED;
        int page = 0;
        int size = 10;
        String sortField = "createAt";
        String sortDir = "desc";

        List<Quotation> filteredList = Collections.singletonList(quotation2); // Only APPROVED quotation
        Pageable pageable = PageRequest.of(page, size);
        Page<Quotation> quotationPage = new PageImpl<>(filteredList, pageable, filteredList.size());

        List<QuotationDTO> filteredDTOList = Arrays.asList(quotationDTO2); // Only APPROVED DTO

        when(quotationRepository.searchAndFilter(eq(search), eq(status), any(Pageable.class)))
                .thenReturn(quotationPage);
        when(quotationMapper.toListDTO(quotationPage)).thenReturn(filteredDTOList);

        // Act
        Page<QuotationDTO> result = quotationService.getAllQuotation(search, status, page, size, sortField, sortDir);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(quotationDTO2, result.getContent().get(0));
        assertEquals(BaseEnum.APPROVED, result.getContent().get(0).getIsAccepted());

        verify(quotationRepository).searchAndFilter(eq(search), eq(status), any(Pageable.class));
        verify(quotationMapper).toListDTO(quotationPage);
    }

    @Test
    @DisplayName("Should get all quotations with paging parameters")
    void getAllQuotationWithPagingParameters() {
        // Arrange
        String search = null;
        BaseEnum status = null;
        int page = 1; // Second page
        int size = 1; // One item per page
        String sortField = "createAt";
        String sortDir = "desc";

        List<Quotation> pagedList = Collections.singletonList(quotation2); // Second page contains only quotation2
        Pageable pageable = PageRequest.of(page, size);
        Page<Quotation> quotationPage = new PageImpl<>(pagedList, pageable, quotationList.size());

        List<QuotationDTO> pagedDTOList = Collections.singletonList(quotationDTO2); // Second page DTO

        when(quotationRepository.searchAndFilter(eq(search), eq(status), any(Pageable.class)))
                .thenReturn(quotationPage);
        when(quotationMapper.toListDTO(quotationPage)).thenReturn(pagedDTOList);

        // Act
        Page<QuotationDTO> result = quotationService.getAllQuotation(search, status, page, size, sortField, sortDir);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements()); // Total records is still 2
        assertEquals(1, result.getContent().size()); // But page size is 1
        assertEquals(quotationDTO2, result.getContent().get(0));
        assertEquals(1, result.getNumber()); // Current page is 1 (second page)

        verify(quotationRepository).searchAndFilter(eq(search), eq(status), any(Pageable.class));
        verify(quotationMapper).toListDTO(quotationPage);
    }

    @Test
    @DisplayName("Should return empty page when no quotations found")
    void getAllQuotationEmptyResult() {
        // Arrange
        String search = "nonexistent";
        BaseEnum status = null;
        int page = 0;
        int size = 10;
        String sortField = "createAt";
        String sortDir = "desc";

        List<Quotation> emptyList = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<Quotation> emptyPage = new PageImpl<>(emptyList, pageable, 0);

        List<QuotationDTO> emptyDTOList = new ArrayList<>();

        when(quotationRepository.searchAndFilter(eq(search), eq(status), any(Pageable.class)))
                .thenReturn(emptyPage);
        when(quotationMapper.toListDTO(emptyPage)).thenReturn(emptyDTOList);

        // Act
        Page<QuotationDTO> result = quotationService.getAllQuotation(search, status, page, size, sortField, sortDir);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(quotationRepository).searchAndFilter(eq(search), eq(status), any(Pageable.class));
        verify(quotationMapper).toListDTO(emptyPage);
    }

    @Test
    @DisplayName("Should get quotation info by id")
    void getQuotationInfo() {
        // Arrange
        when(quotationRepository.findById(3L)).thenReturn(Optional.of(quotationForInfo));
        when(rfqService.getRfqById(rfqForQuotationInfo.getId())).thenReturn(rfqForQuotationInfo);

        // Mock the findQuotationDetail calls based on color conditions
        when(quotationRepository.findQuotationDetail(eq(1L), eq(1L), eq(1L), eq(false)))
                .thenReturn(mockProjection1);
        when(quotationRepository.findQuotationDetail(eq(2L), eq(2L), eq(2L), eq(true)))
                .thenReturn(mockProjection2);

        // Act
        QuotationInfoDTO result = quotationService.getQuotationInfo(3L);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getQuotationId());
        assertEquals("testUser", result.getUserName());
        assertEquals("Test Company", result.getCompanyName());
        assertEquals("1234567890", result.getTaxNumber());
        assertEquals("123 Test St", result.getCompanyAddress());
        assertEquals("APPROVED", result.getIsAccepted());
        assertEquals(1L, result.getSolutionId());

        // Check products list
        assertNotNull(result.getProducts());
        assertEquals(2, result.getProducts().size());

        // Check first product (no color - white)
        QuotationDetailDTO product1 = result.getProducts().get(0);
        assertEquals("Product 1", product1.getProductName());
        assertEquals("Brand 1", product1.getBrandName());
        assertEquals("Category 1", product1.getCategoryName());
        assertEquals(new BigDecimal("100.00"), product1.getPrice());
        assertEquals("#FFFFFF", product1.getNoteColor());
        assertEquals(5, product1.getQuantity());

        // Check second product (with color - red)
        QuotationDetailDTO product2 = result.getProducts().get(1);
        assertEquals("Product 2", product2.getProductName());
        assertEquals("Brand 2", product2.getBrandName());
        assertEquals("Category 2", product2.getCategoryName());
        assertEquals(new BigDecimal("150.00"), product2.getPrice());
        assertEquals("#FF0000", product2.getNoteColor());
        assertEquals(10, product2.getQuantity());

        // Verify calls to repositories and services
        verify(quotationRepository).findById(3L);
        verify(rfqService).getRfqById(rfqForQuotationInfo.getId());
        verify(quotationRepository).findQuotationDetail(1L, 1L, 1L, false);
        verify(quotationRepository).findQuotationDetail(2L, 2L, 2L, true);
    }

    @Test
    @DisplayName("Should throw exception when quotation not found")
    void getQuotationInfoThrowExceptionWhenQuotationNotFound() {
        // Arrange
        when(quotationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            quotationService.getQuotationInfo(999L);
        });

        assertEquals("Quotation not found", exception.getMessage());

        verify(quotationRepository).findById(999L);
        verifyNoInteractions(rfqService);
    }

    @Test
    @DisplayName("Should throw exception when page is less than 0")
    void getAllQuotationThrowExceptionWhenPageLessThanZero() {
        // Arrange
        String search = null;
        BaseEnum status = null;
        int page = -1; // Invalid page
        int size = 10;
        String sortField = "createAt";
        String sortDir = "desc";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            quotationService.getAllQuotation(search, status, page, size, sortField, sortDir);
        });

        assertEquals("Page index must not be less than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when page size is less than 1")
    void getAllQuotationThrowExceptionWhenPageSizeLessThanOne() {
        // Arrange
        String search = null;
        BaseEnum status = null;
        int page = 0;
        int size = 0; // Invalid size
        String sortField = "createAt";
        String sortDir = "desc";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            quotationService.getAllQuotation(search, status, page, size, sortField, sortDir);
        });

        assertEquals("Page size must not be less than one", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when quotation details projections are empty")
    void getQuotationInfoThrowExceptionWhenDetailsEmpty() {
        // Arrange
        when(quotationRepository.findById(3L)).thenReturn(Optional.of(quotationForInfo));
        when(rfqService.getRfqById(rfqForQuotationInfo.getId())).thenReturn(rfqForQuotationInfo);

        // Return null for both projections to simulate empty details
        when(quotationRepository.findQuotationDetail(anyLong(), anyLong(), anyLong(), anyBoolean()))
                .thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            quotationService.getQuotationInfo(3L);
        });

        assertEquals("Quotation not found", exception.getMessage());

        verify(quotationRepository).findById(3L);
        verify(rfqService).getRfqById(rfqForQuotationInfo.getId());
        verify(quotationRepository, times(2)).findQuotationDetail(anyLong(), anyLong(), anyLong(), anyBoolean());
    }

    // Inner class for mocking QuotationInfoProjection
        private record MockQuotationInfoProjection(Long getQuotationId, String getUserName, String getCompanyName, String getTaxNumber,
                                                   String getCompanyAddress, BaseEnum getIsAccepted, Long getSolutionId,
                                                   String getProductName, String getBrandName, String getCategoryName,
                                                   Integer getQuantity, BigDecimal getPrice, String getNoteColor,
                                                   LocalDate getExpectedDate,
                                                   LocalDate getActualDate) implements QuotationInfoProjection {
        }
}
