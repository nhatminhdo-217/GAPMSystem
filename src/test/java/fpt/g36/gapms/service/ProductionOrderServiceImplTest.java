package fpt.g36.gapms.service;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDTO;
import fpt.g36.gapms.models.dto.production_order.ProductionOrderDetailDTO;
import fpt.g36.gapms.models.dto.technical.TechnicalProductionOrderDTO;
import fpt.g36.gapms.models.dto.technical.TechnicalProductionOrderDetailsDTO;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.models.entities.Thread;
import fpt.g36.gapms.models.mapper.ProductionOrderMapper;
import fpt.g36.gapms.models.mapper.PurchaseOrderMapper;
import fpt.g36.gapms.repositories.ProductionOrderDetailRepository;
import fpt.g36.gapms.repositories.ProductionOrderRepository;
import fpt.g36.gapms.repositories.WorkOrderRepository;
import fpt.g36.gapms.services.PurchaseOrderService;
import fpt.g36.gapms.services.impls.ProductionOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionOrderServiceImplTest {

    @Mock
    private ProductionOrderRepository productionOrderRepository;

    @Mock
    private ProductionOrderMapper productionOrderMapper;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private ProductionOrderDetailRepository productionOrderDetailRepository;

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;

    @InjectMocks
    private ProductionOrderServiceImpl productionOrderService;

    // Common test data
    private ProductionOrder productionOrder1;
    private ProductionOrder productionOrder2;
    private ProductionOrderDetail productionOrderDetail1;
    private ProductionOrderDetail productionOrderDetail2;
    private PurchaseOrder purchaseOrder;
    private PurchaseOrderDetail purchaseOrderDetail;
    private User user;
    private WorkOrder workOrder;
    private Thread thread;
    private Product product;
    private ProductionOrderDTO productionOrderDTO1;
    private ProductionOrderDTO productionOrderDTO2;
    private ProductionOrderDetailDTO productionOrderDetailDTO;

    @BeforeEach
    void setUp() {
        // Set up common test data
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        // Create PurchaseOrder
        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);
        purchaseOrder.setStatus(BaseEnum.APPROVED);

        // Create PurchaseOrderDetail
        purchaseOrderDetail = new PurchaseOrderDetail();
        purchaseOrderDetail.setId(1L);
        purchaseOrderDetail.setPurchaseOrder(purchaseOrder);
        purchaseOrderDetail.setQuantity(10);

        // Create Thread with conversion rate
        thread = new Thread();
        thread.setId(1L);
        thread.setConvert_rate(new BigDecimal("1.5"));

        // Create Product
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setThread(thread);
        purchaseOrderDetail.setProduct(product);

        List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();
        purchaseOrderDetails.add(purchaseOrderDetail);
        purchaseOrder.setPurchaseOrderDetails(purchaseOrderDetails);

        // Create ProductionOrder 1 (NOT_APPROVED)
        productionOrder1 = new ProductionOrder();
        productionOrder1.setId(1L);
        productionOrder1.setStatus(BaseEnum.NOT_APPROVED);
        productionOrder1.setPurchaseOrder(purchaseOrder);
        productionOrder1.setCreatedBy(user);
        productionOrder1.setCreateAt(LocalDateTime.now());

        // Create ProductionOrder 2 (APPROVED)
        productionOrder2 = new ProductionOrder();
        productionOrder2.setId(2L);
        productionOrder2.setStatus(BaseEnum.APPROVED);
        productionOrder2.setPurchaseOrder(purchaseOrder);
        productionOrder2.setCreatedBy(user);
        productionOrder2.setApprovedBy(user);
        productionOrder2.setCreateAt(LocalDateTime.now().minusDays(1));

        // Create ProductionOrderDetail 1
        productionOrderDetail1 = new ProductionOrderDetail();
        productionOrderDetail1.setId(1L);
        productionOrderDetail1.setProductionOrder(productionOrder1);
        productionOrderDetail1.setPurchaseOrderDetail(purchaseOrderDetail);
        productionOrderDetail1.setThread_mass(new BigDecimal("15.0")); // 10 * 1.5
        productionOrderDetail1.setLight_env(true);

        // Create ProductionOrderDetail 2
        productionOrderDetail2 = new ProductionOrderDetail();
        productionOrderDetail2.setId(2L);
        productionOrderDetail2.setProductionOrder(productionOrder2);
        productionOrderDetail2.setPurchaseOrderDetail(purchaseOrderDetail);
        productionOrderDetail2.setThread_mass(new BigDecimal("15.0")); // 10 * 1.5
        productionOrderDetail2.setLight_env(false);

        List<ProductionOrderDetail> productionOrderDetails1 = new ArrayList<>();
        productionOrderDetails1.add(productionOrderDetail1);
        productionOrder1.setProductionOrderDetails(productionOrderDetails1);

        List<ProductionOrderDetail> productionOrderDetails2 = new ArrayList<>();
        productionOrderDetails2.add(productionOrderDetail2);
        productionOrder2.setProductionOrderDetails(productionOrderDetails2);

        // Create WorkOrder
        workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setProductionOrder(productionOrder2);
        workOrder.setStatus(BaseEnum.APPROVED);
        workOrder.setCreatedBy(user);

        // Create DTOs
        productionOrderDTO1 = new ProductionOrderDTO();
        productionOrderDTO1.setId(1L);
        productionOrderDTO1.setStatus(BaseEnum.NOT_APPROVED);
        productionOrderDTO1.setCreatedBy("testUser");

        productionOrderDTO2 = new ProductionOrderDTO();
        productionOrderDTO2.setId(2L);
        productionOrderDTO2.setStatus(BaseEnum.APPROVED);
        productionOrderDTO2.setCreatedBy("testUser");
        productionOrderDTO2.setApprovedBy("testUser");

        productionOrderDetailDTO = new ProductionOrderDetailDTO();
        productionOrderDetailDTO.setId(1L);
        productionOrderDetailDTO.setThreadMass(new BigDecimal("15.0"));
        productionOrderDetailDTO.setLightEnv(true);
    }

    @Nested
    @DisplayName("getApprovedProductionOrders Tests")
    class GetApprovedProductionOrdersTests {

        @Test
        @DisplayName("Should return all approved production orders")
        void shouldReturnAllApprovedProductionOrders() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<ProductionOrder> productionOrders = List.of(productionOrder2);
            Page<ProductionOrder> productionOrderPage = new PageImpl<>(productionOrders, pageable, 1);

            when(productionOrderRepository.findAllByStatus(BaseEnum.APPROVED, pageable))
                    .thenReturn(productionOrderPage);
            when(workOrderRepository.findByProductionOrder(productionOrder2))
                    .thenReturn(workOrder);

            // Act
            Page<TechnicalProductionOrderDTO> result = productionOrderService.getApprovedProductionOrders(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(productionOrder2.getId(), result.getContent().get(0).getId());
            assertEquals(BaseEnum.APPROVED, result.getContent().get(0).getStatus());
            assertTrue(result.getContent().get(0).isHasWorkOrder());

            verify(productionOrderRepository).findAllByStatus(BaseEnum.APPROVED, pageable);
            verify(workOrderRepository).findByProductionOrder(productionOrder2);
        }

        @Test
        @DisplayName("Should return empty page when no approved production orders")
        void shouldReturnEmptyPageWhenNoApprovedProductionOrders() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductionOrder> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(productionOrderRepository.findAllByStatus(BaseEnum.APPROVED, pageable))
                    .thenReturn(emptyPage);

            // Act
            Page<TechnicalProductionOrderDTO> result = productionOrderService.getApprovedProductionOrders(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());

            verify(productionOrderRepository).findAllByStatus(BaseEnum.APPROVED, pageable);
            verifyNoInteractions(workOrderRepository);
        }
    }

    @Nested
    @DisplayName("getProductionOrderDetails Tests")
    class GetProductionOrderDetailsTests {

        @Test
        @DisplayName("Should return production order details")
        void shouldReturnProductionOrderDetails() {
            // Arrange
            when(productionOrderRepository.findById(2L)).thenReturn(Optional.of(productionOrder2));
            when(workOrderRepository.findByProductionOrder(productionOrder2)).thenReturn(workOrder);

            // Act
            TechnicalProductionOrderDetailsDTO result = productionOrderService.getProductionOrderDetails(2L);

            // Assert
            assertNotNull(result);
            assertEquals(2L, result.getId());
            assertEquals(BaseEnum.APPROVED, result.getStatus());
            assertEquals(user, result.getCreatedBy());
            assertEquals(user, result.getApprovedBy());
            assertEquals(purchaseOrder, result.getPurchaseOrder());
            assertEquals(workOrder, result.getWorkOrder());
            assertEquals(1, result.getProductionOrderDetails().size());
            assertEquals(productionOrderDetail2.getId(), result.getProductionOrderDetails().get(0).getId());
            assertEquals(productionOrderDetail2.getThread_mass(), result.getProductionOrderDetails().get(0).getThreadMass());
            assertEquals(productionOrderDetail2.getLight_env(), result.getProductionOrderDetails().get(0).getLightEnv());

            verify(productionOrderRepository).findById(2L);
            verify(workOrderRepository).findByProductionOrder(productionOrder2);
        }

        @Test
        @DisplayName("Should throw exception when production order not found")
        void shouldThrowExceptionWhenProductionOrderNotFound() {
            // Arrange
            when(productionOrderRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                productionOrderService.getProductionOrderDetails(999L);
            });

            assertEquals("Không tìm thấy Production Order với ID: 999", exception.getMessage());

            verify(productionOrderRepository).findById(999L);
            verifyNoInteractions(workOrderRepository);
        }
    }

    @Nested
    @DisplayName("getProductionOrderById Tests")
    class GetProductionOrderByIdTests {

        @Test
        @DisplayName("Should return production order by id")
        void shouldReturnProductionOrderById() {
            // Arrange
            when(productionOrderRepository.findById(1L)).thenReturn(Optional.of(productionOrder1));

            // Act
            ProductionOrder result = productionOrderService.getProductionOrderById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(BaseEnum.NOT_APPROVED, result.getStatus());

            verify(productionOrderRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when production order not found")
        void shouldThrowExceptionWhenProductionOrderNotFound() {
            // Arrange
            when(productionOrderRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                productionOrderService.getProductionOrderById(999L);
            });

            assertEquals("Không tìm thấy Production Order với ID: 999", exception.getMessage());

            verify(productionOrderRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("findDetailByProductionOrderId Tests")
    class FindDetailByProductionOrderIdTests {

        @Test
        @DisplayName("Should return production order details by production order id")
        void shouldReturnProductionOrderDetailsByProductionOrderId() {
            // Arrange
            List<ProductionOrderDetail> details = List.of(productionOrderDetail1);
            List<ProductionOrderDetailDTO> detailDTOs = List.of(productionOrderDetailDTO);

            when(productionOrderRepository.findAllByProductionOrderId(1L)).thenReturn(details);
            when(productionOrderMapper.toDetailDTO(productionOrderDetail1)).thenReturn(productionOrderDetailDTO);

            // Act
            List<ProductionOrderDetailDTO> result = productionOrderService.findDetailByProductionOrderId(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals(new BigDecimal("15.0"), result.get(0).getThreadMass());
            assertTrue(result.get(0).isLightEnv());

            verify(productionOrderRepository).findAllByProductionOrderId(1L);
            verify(productionOrderMapper).toDetailDTO(productionOrderDetail1);
        }

        @Test
        @DisplayName("Should return empty list when no details found")
        void shouldReturnEmptyListWhenNoDetailsFound() {
            // Arrange
            when(productionOrderRepository.findAllByProductionOrderId(999L)).thenReturn(List.of());

            // Act
            List<ProductionOrderDetailDTO> result = productionOrderService.findDetailByProductionOrderId(999L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(productionOrderRepository).findAllByProductionOrderId(999L);
            verifyNoInteractions(productionOrderMapper);
        }
    }

    @Nested
    @DisplayName("findDetailById Tests")
    class FindDetailByIdTests {

        @Test
        @DisplayName("Should return production order detail by id")
        void shouldReturnProductionOrderDetailById() {
            // Arrange
            when(productionOrderRepository.findByProductionOrderId(1L)).thenReturn(Optional.of(productionOrderDetail1));
            when(productionOrderMapper.toDetailDTO(productionOrderDetail1)).thenReturn(productionOrderDetailDTO);

            // Act
            ProductionOrderDetailDTO result = productionOrderService.findDetailById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(new BigDecimal("15.0"), result.getThreadMass());
            assertTrue(result.isLightEnv());

            verify(productionOrderRepository).findByProductionOrderId(1L);
            verify(productionOrderMapper).toDetailDTO(productionOrderDetail1);
        }

        @Test
        @DisplayName("Should throw exception when production order detail not found")
        void shouldThrowExceptionWhenProductionOrderDetailNotFound() {
            // Arrange
            when(productionOrderRepository.findByProductionOrderId(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                productionOrderService.findDetailById(999L);
            });

            assertEquals("Production Order Detail not found", exception.getMessage());

            verify(productionOrderRepository).findByProductionOrderId(999L);
            verifyNoInteractions(productionOrderMapper);
        }
    }

    @Nested
    @DisplayName("updateProductionOrderDetail Tests")
    class UpdateProductionOrderDetailTests {

        @Test
        @DisplayName("Should update production order detail")
        void shouldUpdateProductionOrderDetail() {
            // Arrange
            ProductionOrderDetailDTO inputDTO = new ProductionOrderDetailDTO();
            inputDTO.setId(1L);
            inputDTO.setThreadMass(new BigDecimal("20.0"));
            inputDTO.setLightEnv(false);

            ProductionOrderDetail updatedDetail = new ProductionOrderDetail();
            updatedDetail.setId(1L);
            updatedDetail.setThread_mass(new BigDecimal("20.0"));
            updatedDetail.setLight_env(false);

            when(productionOrderDetailRepository.findById(1L)).thenReturn(Optional.of(productionOrderDetail1));
            when(productionOrderDetailRepository.save(any(ProductionOrderDetail.class))).thenReturn(updatedDetail);
            when(productionOrderMapper.convertToDTO(updatedDetail)).thenReturn(inputDTO);

            // Act
            ProductionOrderDetailDTO result = productionOrderService.updateProductionOrderDetail(inputDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(new BigDecimal("20.0"), result.getThreadMass());
            assertFalse(result.isLightEnv());

            verify(productionOrderDetailRepository).findById(1L);
            verify(productionOrderDetailRepository).save(any(ProductionOrderDetail.class));
            verify(productionOrderMapper).convertToDTO(updatedDetail);
        }

        @Test
        @DisplayName("Should throw exception when production order detail not found")
        void shouldThrowExceptionWhenProductionOrderDetailNotFound() {
            // Arrange
            ProductionOrderDetailDTO inputDTO = new ProductionOrderDetailDTO();
            inputDTO.setId(999L);

            when(productionOrderDetailRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                productionOrderService.updateProductionOrderDetail(inputDTO);
            });

            assertEquals("Không tìm thấy chi tiết đơn hàng sản xuất với ID: 999", exception.getMessage());

            verify(productionOrderDetailRepository).findById(999L);
            verifyNoInteractions(productionOrderMapper);
        }
    }

    @Nested
    @DisplayName("createProductionOrder Tests")
    class CreateProductionOrderTests {

        @Test
        @DisplayName("Should create production order from purchase order")
        void shouldCreateProductionOrderFromPurchaseOrder() {
            // Arrange
            when(purchaseOrderService.getPurchaseOrderById(1L)).thenReturn(Optional.of(purchaseOrder));
            when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder1);
            when(purchaseOrderService.getPurchaseOrderDetailById(1L)).thenReturn(purchaseOrderDetail);

            // Act
            productionOrderService.createProductionOrder(1L);

            // Assert
            verify(purchaseOrderService, times(2)).getPurchaseOrderById(1L);
            verify(productionOrderRepository).save(any(ProductionOrder.class));
            verify(productionOrderDetailRepository).save(any(ProductionOrderDetail.class));
            verify(purchaseOrderService).getPurchaseOrderDetailById(1L);
        }

        @Test
        @DisplayName("Should throw exception when purchase order not found")
        void shouldThrowExceptionWhenPurchaseOrderNotFound() {
            // Arrange
            when(purchaseOrderService.getPurchaseOrderById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                productionOrderService.createProductionOrder(999L);
            });

            assertEquals("Không tìm thấy đơn hàng", exception.getMessage());

            verify(purchaseOrderService).getPurchaseOrderById(999L);
            verifyNoInteractions(productionOrderRepository);
            verifyNoInteractions(productionOrderDetailRepository);
        }
    }

    @Nested
    @DisplayName("getStatusByProductionOrder Tests")
    class GetStatusByProductionOrderTests {

        @Test
        @DisplayName("Should return status when production order exists")
        void shouldReturnStatusWhenProductionOrderExists() {
            // Arrange
            when(productionOrderRepository.findById(1L)).thenReturn(Optional.of(productionOrder1));

            // Act
            BaseEnum result = productionOrderService.getStatusByProductionOrder(1L);

            // Assert
            assertEquals(BaseEnum.NOT_APPROVED, result);

            verify(productionOrderRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when production order not found")
        void shouldReturnNullWhenProductionOrderNotFound() {
            // Arrange
            when(productionOrderRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                BaseEnum result = productionOrderService.getStatusByProductionOrder(999L);
            });

            // Assert
            assertEquals("Không tìm thấy lệnh sản xuất", exception.getMessage());

            verify(productionOrderRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("updateStatus Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update status from DRAFT to NOT_APPROVED")
        void shouldUpdateStatusFromDraftToNotApproved() {
            // Arrange
            ProductionOrder draftOrder = new ProductionOrder();
            draftOrder.setId(3L);
            draftOrder.setStatus(BaseEnum.DRAFT);

            when(productionOrderRepository.findById(3L)).thenReturn(Optional.of(draftOrder));
            when(productionOrderRepository.save(any(ProductionOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            ProductionOrder result = productionOrderService.updateStatus(3L, user);

            // Assert
            assertNotNull(result);
            assertEquals(BaseEnum.NOT_APPROVED, result.getStatus());
            assertEquals(user, result.getCreatedBy());

            // Verify findById is called twice - once in updateStatus and once in getStatusByProductionOrderId
            verify(productionOrderRepository, times(2)).findById(3L);
            verify(productionOrderRepository).save(any(ProductionOrder.class));
        }

        @Test
        @DisplayName("Should update status from NOT_APPROVED to APPROVED")
        void shouldUpdateStatusFromNotApprovedToApproved() {
            // Arrange
            when(productionOrderRepository.findById(1L)).thenReturn(Optional.of(productionOrder1));
            when(productionOrderRepository.save(any(ProductionOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            ProductionOrder result = productionOrderService.updateStatus(1L, user);

            // Assert
            assertNotNull(result);
            assertEquals(BaseEnum.APPROVED, result.getStatus());
            assertEquals(user, result.getCreatedBy());
            assertEquals(user, result.getApprovedBy());

            // Verify findById is called three times
            verify(productionOrderRepository, times(3)).findById(1L);
            verify(productionOrderRepository).save(any(ProductionOrder.class));
        }

        @Test
        @DisplayName("Should throw exception when production order not found")
        void shouldThrowExceptionWhenProductionOrderNotFound() {
            // Arrange
            when(productionOrderRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                productionOrderService.updateStatus(999L, user);
            });

            assertEquals("Không tìm thấy lệnh sản xuất", exception.getMessage());

            verify(productionOrderRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("cancelProductionOrder Tests")
    class CancelProductionOrderTests {

        @Test
        @DisplayName("Should cancel production order if not approved")
        void shouldCancelProductionOrderIfNotApproved() {
            // Arrange
            when(productionOrderRepository.findById(1L)).thenReturn(Optional.of(productionOrder1));
            when(productionOrderRepository.save(any(ProductionOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            boolean result = productionOrderService.cancelProductionOrder(1L);

            // Assert
            assertTrue(result);
            assertEquals(BaseEnum.CANCELED, productionOrder1.getStatus());

            verify(productionOrderRepository).findById(1L);
            verify(productionOrderRepository).save(productionOrder1);
        }

        @Test
        @DisplayName("Should not cancel production order if already approved")
        void shouldNotCancelProductionOrderIfAlreadyApproved() {
            // Arrange
            when(productionOrderRepository.findById(2L)).thenReturn(Optional.of(productionOrder2));

            // Act
            boolean result = productionOrderService.cancelProductionOrder(2L);

            // Assert
            assertFalse(result);
            assertEquals(BaseEnum.APPROVED, productionOrder2.getStatus());

            verify(productionOrderRepository).findById(2L);
            verifyNoMoreInteractions(productionOrderRepository);
        }

        @Test
        @DisplayName("Should throw exception when production order not found")
        void shouldThrowExceptionWhenProductionOrderNotFound() {
            // Arrange
            when(productionOrderRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                productionOrderService.cancelProductionOrder(999L);
            });

            assertEquals("Production Order not found", exception.getMessage());

            verify(productionOrderRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("getAllProductionOrders Tests")
    class GetAllProductionOrdersTests {

        @Test
        @DisplayName("Should get all production orders with filters")
        void shouldGetAllProductionOrdersWithFilters() {
            // Arrange
            String search = "test";
            BaseEnum status = BaseEnum.NOT_APPROVED;
            int page = 0;
            int size = 10;
            String sortField = "createAt";
            String sortDir = "desc";

            Pageable pageable = PageRequest.of(page, size);
            List<ProductionOrder> productionOrders = List.of(productionOrder1);
            Page<ProductionOrder> productionOrderPage = new PageImpl<>(productionOrders, pageable, 1);
            List<ProductionOrderDTO> productionOrderDTOs = List.of(productionOrderDTO1);

            when(productionOrderRepository.searchAndFilter(search, status, pageable))
                    .thenReturn(productionOrderPage);
            when(productionOrderMapper.toDTOList(productionOrders))
                    .thenReturn(productionOrderDTOs);

            // Act
            Page<ProductionOrderDTO> result = productionOrderService.getAllProductionOrders(search, status, page, size, sortField, sortDir);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1L, result.getContent().get(0).getId());
            assertEquals(BaseEnum.NOT_APPROVED, result.getContent().get(0).getStatus());

            verify(productionOrderRepository).searchAndFilter(search, status, pageable);
            verify(productionOrderMapper).toDTOList(productionOrders);
        }

        @Test
        @DisplayName("Should return empty page when no production orders match filters")
        void shouldReturnEmptyPageWhenNoProductionOrdersMatchFilters() {
            // Arrange
            String search = "nonexistent";
            BaseEnum status = BaseEnum.CANCELED;
            int page = 0;
            int size = 10;
            String sortField = "createAt";
            String sortDir = "desc";

            Pageable pageable = PageRequest.of(page, size);
            Page<ProductionOrder> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(productionOrderRepository.searchAndFilter(search, status, pageable))
                    .thenReturn(emptyPage);
            when(productionOrderMapper.toDTOList(List.of()))
                    .thenReturn(List.of());

            // Act
            Page<ProductionOrderDTO> result = productionOrderService.getAllProductionOrders(search, status, page, size, sortField, sortDir);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());

            verify(productionOrderRepository).searchAndFilter(search, status, pageable);
            verify(productionOrderMapper).toDTOList(List.of());
        }

        @Test
        @DisplayName("Should throw exception when page is less than 0")
        void shouldThrowExceptionWhenPageIsLessThanZero() {
            // Arrange
            String search = "test";
            BaseEnum status = BaseEnum.NOT_APPROVED;
            int page = -1;
            int size = 10;
            String sortField = "createAt";
            String sortDir = "desc";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                productionOrderService.getAllProductionOrders(search, status, page, size, sortField, sortDir);
            });

            assertEquals("Page index must not be less than zero", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when size is less than 1")
        void shouldThrowExceptionWhenSizeIsLessThanOne() {
            // Arrange
            String search = "test";
            BaseEnum status = BaseEnum.NOT_APPROVED;
            int page = 0;
            int size = 0;
            String sortField = "createAt";
            String sortDir = "desc";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                productionOrderService.getAllProductionOrders(search, status, page, size, sortField, sortDir);
            });

            assertEquals("Page size must not be less than one", exception.getMessage());
        }
    }
}