package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.dto.quotation.QuotationInfoProjection;
import fpt.g36.gapms.models.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuotationRepositoryTest {

    @Mock
    private QuotationRepository quotationRepository;

    private Quotation quotation;
    private Rfq rfq;
    private User user;
    private Company company;
    private Product product;
    private Brand brand;
    private Category category;
    private Solution solution;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@gmail.com");

        company = new Company();
        company.setId(1L);
        company.setName("Test Company");
        company.setTaxNumber("123456789");
        company.setAddress("Test Address");

        rfq = new Rfq();
        rfq.setId(1L);
        rfq.setCreateBy(user);
        rfq.setExpectDeliveryDate(LocalDate.now().plusDays(30));
        rfq.setIsSent(BaseEnum.APPROVED);
        rfq.setIsApproved(SendEnum.SENT);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");
        brand.setProduction(product);

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        RfqDetail rfqDetail = new RfqDetail();
        rfqDetail.setId(1L);
        rfqDetail.setRfq(rfq);
        rfqDetail.setProduct(product);
        rfqDetail.setBrand(brand);
        rfqDetail.setCate(category);
        rfqDetail.setQuantity(10);
        rfqDetail.setNoteColor("#FF0000");

        List<RfqDetail> rfqDetails = new ArrayList<>();
        rfqDetails.add(rfqDetail);
        rfq.setRfqDetails(rfqDetails);

        solution = new Solution();
        solution.setId(1L);
        solution.setRfq(rfq);
        solution.setCreateBy(user);
        solution.setActualDeliveryDate(LocalDate.now().plusDays(45));
        solution.setIsSent(SendEnum.SENT);

        rfq.setSolution(solution);

        quotation = new Quotation();
        quotation.setId(1L);
        quotation.setRfq(rfq);
        quotation.setIsCanceled(false);
        quotation.setIsAccepted(BaseEnum.NOT_APPROVED);
        quotation.setCreatedBy(user);
        quotation.setCreateAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should search and filter quotations")
    void searchAndFilter(){
        Pageable pageable = PageRequest.of(0, 10);
        List<Quotation> quotations = Collections.singletonList(quotation);
        Page<Quotation> expectedPage = new PageImpl<>(quotations, pageable, 1);

        when(quotationRepository.searchAndFilter(anyString(), any(BaseEnum.class), any(Pageable.class))).thenReturn(expectedPage);

        // Test case 1: Search with null parameters
        Page<Quotation> result1 = quotationRepository.searchAndFilter(null, null, pageable);

        //Assert
        assertNotNull(result1);
        assertEquals(1, result1.getTotalElements());
        assertEquals(quotation, result1.getContent().get(0));
        verify(quotationRepository).searchAndFilter(null, null, pageable);

        // Test case 2: Search with specific parameters
        reset(quotationRepository);
        when(quotationRepository.searchAndFilter(eq("product"), eq(BaseEnum.APPROVED), any(Pageable.class))).thenReturn(expectedPage);

        //Act
        Page<Quotation> result2 = quotationRepository.searchAndFilter("product", BaseEnum.APPROVED, pageable);

        //Assert
        assertNotNull(result2);
        assertEquals(1, result2.getTotalElements());
        assertEquals(quotation, result2.getContent().get(0));
        verify(quotationRepository).searchAndFilter("product", BaseEnum.APPROVED, pageable);

        // Test case 3: Search with empty results
        reset(quotationRepository);
        Page<Quotation> emtyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(quotationRepository.searchAndFilter(eq("nonexistent"), any(), any(Pageable.class))).thenReturn(emtyPage);

        //Act
        Page<Quotation> result3 = quotationRepository.searchAndFilter("nonexistent", null, pageable);

        //Assert
        assertNotNull(result3);
        assertEquals(0, result3.getTotalElements());
        assertTrue(result3.getContent().isEmpty());
        verify(quotationRepository).searchAndFilter("nonexistent", null, pageable);
    }
}
