package fpt.g36.gapms.service;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.enums.StageType;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.models.entities.Thread;
import fpt.g36.gapms.repositories.*;
import fpt.g36.gapms.services.MachineService;
import fpt.g36.gapms.services.WorkOrderService;
import fpt.g36.gapms.services.impls.WorkOrderServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkOrderServiceImplTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private MachineService machineService;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private DyeMachineRepository dyeMachineRepository;

    @Mock
    private WindingMachineRepository windingMachineRepository;

    @Mock
    private WorkOrderDetailsRepository workOrderDetailRepository;

    @Mock
    private DyeStageRepository dyeStageRepository;

    @Mock
    private WindingStageRepository windingStageRepository;

    @Mock
    private PackagingStageRepository packagingStageRepository;

    @Mock
    private DyeBatchRepository dyeBatchRepository;

    @Mock
    private WindingBatchRepository windingBatchRepository;

    @Mock
    private PackagingBatchRepository packagingBatchRepository;

    @Mock
    private DyeRiskAssessmentRepository dyeRiskAssessmentRepository;

    @Mock
    private WindingRiskAssessmentRepository windingRiskAssessmentRepository;

    @Mock
    private PackagingRiskAssessmentRepository packagingRiskAssessmentRepository;

    @Mock
    private RiskSolutionRepository riskSolutionRepository;

    @Mock
    private PhotoStageRepository photoStageRepository;

    @Mock
    private EntityManager entityManager;

    @Spy
    @InjectMocks
    private WorkOrderServiceImpl workOrderService;

    // Test data
    private WorkOrder workOrder;
    private ProductionOrder productionOrder;
    private PurchaseOrder purchaseOrder;
    private PurchaseOrderDetail purchaseOrderDetail;
    private ProductionOrderDetail productionOrderDetail;
    private User user;
    private Solution solution;
    private WorkOrderDetail workOrderDetail;
    private Thread thread;
    private DyeMachine dyeMachine;
    private WindingMachine windingMachine;
    private List<Long> dyeMachineIds;
    private List<Long> windingMachineIds;
    private List<BigDecimal> additionalWeights;
    private Shift shift;

    @BeforeEach
    void setUp() {
        // Setup for common test data
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        // Setup Thread
        thread = new Thread();
        thread.setId(1L);
        thread.setName("Test Thread");
        thread.setConvert_rate(new BigDecimal("1.5")); // 1.5kg per product

        // Setup Product
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setThread(thread);

        // Setup Solution
        solution = new Solution();
        solution.setId(1L);
        solution.setActualDeliveryDate(LocalDate.now().plusDays(10));

        // Setup PurchaseOrder
        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);
        purchaseOrder.setStatus(BaseEnum.APPROVED);
        purchaseOrder.setSolution(solution);

        // Setup PurchaseOrderDetail
        purchaseOrderDetail = new PurchaseOrderDetail();
        purchaseOrderDetail.setId(1L);
        purchaseOrderDetail.setPurchaseOrder(purchaseOrder);
        purchaseOrderDetail.setQuantity(10); // 10 products
        purchaseOrderDetail.setProduct(product);

        List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();
        purchaseOrderDetails.add(purchaseOrderDetail);
        purchaseOrder.setPurchaseOrderDetails(purchaseOrderDetails);

        // Setup ProductionOrder
        productionOrder = new ProductionOrder();
        productionOrder.setId(1L);
        productionOrder.setPurchaseOrder(purchaseOrder);
        productionOrder.setStatus(BaseEnum.APPROVED);
        productionOrder.setCreatedBy(user);
        productionOrder.setApprovedBy(user);

        // Setup ProductionOrderDetail
        productionOrderDetail = new ProductionOrderDetail();
        productionOrderDetail.setId(1L);
        productionOrderDetail.setProductionOrder(productionOrder);
        productionOrderDetail.setPurchaseOrderDetail(purchaseOrderDetail);
        productionOrderDetail.setThread_mass(new BigDecimal("15.0")); // 10 * 1.5 = 15kg
        productionOrderDetail.setLight_env(true);

        List<ProductionOrderDetail> productionOrderDetails = new ArrayList<>();
        productionOrderDetails.add(productionOrderDetail);
        productionOrder.setProductionOrderDetails(productionOrderDetails);

        // Setup WorkOrder
        workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setProductionOrder(productionOrder);
        workOrder.setDeadline(LocalDate.now().plusDays(7));
        workOrder.setStatus(BaseEnum.DRAFT);
        workOrder.setSendStatus(SendEnum.NOT_SENT);
        workOrder.setIsProduction(WorkEnum.NOT_STARTED);
        workOrder.setCreatedBy(user);
        workOrder.setWorkOrderDetails(new ArrayList<>());

        // Setup WorkOrderDetail
        workOrderDetail = new WorkOrderDetail();
        workOrderDetail.setId(1L);
        workOrderDetail.setWorkOrder(workOrder);
        workOrderDetail.setProductionOrderDetail(productionOrderDetail);
        workOrderDetail.setPurchaseOrderDetail(purchaseOrderDetail);
        workOrderDetail.setPlannedStartAt(LocalDateTime.now().plusHours(2));
        workOrderDetail.setPlannedEndAt(LocalDateTime.now().plusDays(6));
        workOrderDetail.setWorkStatus(WorkEnum.NOT_STARTED);
        workOrderDetail.setAdditionalWeight(new BigDecimal("1.0"));

        List<WorkOrderDetail> workOrderDetails = new ArrayList<>();
        workOrderDetails.add(workOrderDetail);
        workOrder.setWorkOrderDetails(workOrderDetails);

        // Setup DyeMachine
        dyeMachine = new DyeMachine();
        dyeMachine.setId(1L);
        dyeMachine.setMaxWeight(new BigDecimal("20.0"));
        dyeMachine.setLittersMin(new BigDecimal("30.0"));
        dyeMachine.setLittersMax(new BigDecimal("120.0"));
        dyeMachine.setConeMin(new BigDecimal("10.0"));
        dyeMachine.setConeMax(new BigDecimal("30.0"));
        dyeMachine.setActive(true);

        // Setup WindingMachine
        windingMachine = new WindingMachine();
        windingMachine.setId(1L);
        windingMachine.setActive(true);

        // Setup machine IDs and additional weights
        dyeMachineIds = Arrays.asList(1L);
        windingMachineIds = Arrays.asList(1L);
        additionalWeights = Arrays.asList(new BigDecimal("1.0"));

        // Setup Shift
        shift = new Shift();
        shift.setId(1L);
        shift.setShiftName("Morning Shift");
        shift.setShiftStart(LocalTime.of(8, 0));
        shift.setShiftEnd(LocalTime.of(16, 0));

        List<UserShift> userShifts = new ArrayList<>();

        // Create roles for team leaders and QAs
        Role dyeLeadRole = new Role();
        dyeLeadRole.setName("LEAD_DYE");

        Role windingLeadRole = new Role();
        windingLeadRole.setName("LEAD_WINDING");

        Role packagingLeadRole = new Role();
        packagingLeadRole.setName("LEAD_PACKAGING");

        Role dyeQaRole = new Role();
        dyeQaRole.setName("QA_DYE");

        Role windingQaRole = new Role();
        windingQaRole.setName("QA_WINDING");

        Role packagingQaRole = new Role();
        packagingQaRole.setName("QA_PACKAGING");

        // Create team leaders and QA users
        User dyeLeader = new User();
        dyeLeader.setId(2L);
        dyeLeader.setRole(dyeLeadRole);

        User windingLeader = new User();
        windingLeader.setId(3L);
        windingLeader.setRole(windingLeadRole);

        User packagingLeader = new User();
        packagingLeader.setId(4L);
        packagingLeader.setRole(packagingLeadRole);

        User dyeQA = new User();
        dyeQA.setId(5L);
        dyeQA.setRole(dyeQaRole);

        User windingQA = new User();
        windingQA.setId(6L);
        windingQA.setRole(windingQaRole);

        User packagingQA = new User();
        packagingQA.setId(7L);
        packagingQA.setRole(packagingQaRole);

        // Create UserShift for each user
        UserShift dyeLeaderShift = new UserShift();
        dyeLeaderShift.setUser(dyeLeader);
        dyeLeaderShift.setShift(shift);

        UserShift windingLeaderShift = new UserShift();
        windingLeaderShift.setUser(windingLeader);
        windingLeaderShift.setShift(shift);

        UserShift packagingLeaderShift = new UserShift();
        packagingLeaderShift.setUser(packagingLeader);
        packagingLeaderShift.setShift(shift);

        UserShift dyeQAShift = new UserShift();
        dyeQAShift.setUser(dyeQA);
        dyeQAShift.setShift(shift);

        UserShift windingQAShift = new UserShift();
        windingQAShift.setUser(windingQA);
        windingQAShift.setShift(shift);

        UserShift packagingQAShift = new UserShift();
        packagingQAShift.setUser(packagingQA);
        packagingQAShift.setShift(shift);

        userShifts.add(dyeLeaderShift);
        userShifts.add(windingLeaderShift);
        userShifts.add(packagingLeaderShift);
        userShifts.add(dyeQAShift);
        userShifts.add(windingQAShift);
        userShifts.add(packagingQAShift);

        shift.setUserShifts(userShifts);
    }

    @Nested
    @DisplayName("Get Work Orders Tests")
    class GetWorkOrdersTests {

        @Test
        @DisplayName("Should get all work orders for team leader with valid pagination")
        void getAllWorkOrderTeamLeader_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            String workOrderId = null;
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.getAllWorkOrderTeamLeader(null, pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getAllWorkOrderTeamLeader(pageable, workOrderId);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).getAllWorkOrderTeamLeader(null, pageable);
        }

        @Test
        @DisplayName("Should get all work orders for team leader with specific workOrderId")
        void getAllWorkOrderTeamLeader_WithWorkOrderId_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            String workOrderId = "WO-1";
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.getAllWorkOrderTeamLeader(1L, pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getAllWorkOrderTeamLeader(pageable, workOrderId);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).getAllWorkOrderTeamLeader(1L, pageable);
        }

        @Test
        @DisplayName("Should get all work orders for PO with valid pagination")
        void getAllWorkOrderPo_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            String workOrderId = null;
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.getAllWorkOrderPo(null, pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getAllWorkOrderPo(pageable, workOrderId);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).getAllWorkOrderPo(null, pageable);
        }

        @Test
        @DisplayName("Should get all work orders")
        void getAllWorkOrders_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.findAllByOrderByCreateAt(pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getAllWorkOrders(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).findAllByOrderByCreateAt(pageable);
        }

        @Test
        @DisplayName("Should get all submitted work orders")
        void getAllSubmittedWorkOrders_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.findAllBySendStatus(SendEnum.SENT, pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getAllSubmittedWorkOrders(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).findAllBySendStatus(SendEnum.SENT, pageable);
        }
    }

    @Nested
    @DisplayName("Get Work Order By ID Tests")
    class GetWorkOrderByIdTests {

        @Test
        @DisplayName("Should get work order by id when it exists")
        void getWorkOrderById_Success() {
            // Arrange
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));

            // Act
            WorkOrder result = workOrderService.getWorkOrderById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(workOrderRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when work order not found")
        void getWorkOrderById_NotFound_ThrowsException() {
            // Arrange
            when(workOrderRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                workOrderService.getWorkOrderById(999L);
            });

            assertEquals("Không tìm thấy Work Order với ID: 999", exception.getMessage());
            verify(workOrderRepository).findById(999L);
        }

        @Test
        @DisplayName("Should get submitted work order by id")
        void getSubmittedWorkOrderById_Success() {
            // Arrange
            workOrder.setSendStatus(SendEnum.SENT);
            when(workOrderRepository.findByIdAndSendStatus(1L, SendEnum.SENT)).thenReturn(workOrder);

            // Act
            WorkOrder result = workOrderService.getSubmittedWorkOrderById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(SendEnum.SENT, result.getSendStatus());
            verify(workOrderRepository).findByIdAndSendStatus(1L, SendEnum.SENT);
        }
    }

    @Nested
    @DisplayName("Submit Work Order Tests")
    class SubmitWorkOrderTests {

        @Test
        @DisplayName("Should submit work order successfully")
        void submitWorkOrder_Success() {
            // Arrange
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
            when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(workOrder);

            // Act
            WorkOrder result = workOrderService.submitWorkOrder(1L);

            // Assert
            assertNotNull(result);
            assertEquals(SendEnum.SENT, result.getSendStatus());
            assertEquals(BaseEnum.WAIT_FOR_APPROVAL, result.getStatus());
            verify(workOrderRepository).findById(1L);
            verify(workOrderRepository).save(workOrder);
        }

        @Test
        @DisplayName("Should throw exception when work order already submitted")
        void submitWorkOrder_AlreadySubmitted_ThrowsException() {
            // Arrange
            workOrder.setSendStatus(SendEnum.SENT);
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                workOrderService.submitWorkOrder(1L);
            });

            assertEquals("Work Order đã được gửi trước đó.", exception.getMessage());
            verify(workOrderRepository).findById(1L);
            verify(workOrderRepository, never()).save(any(WorkOrder.class));
        }

        @Test
        @DisplayName("Should throw exception when work order not found")
        void submitWorkOrder_NotFound_ThrowsException() {
            // Arrange
            when(workOrderRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                workOrderService.submitWorkOrder(999L);
            });

            assertEquals("Work Order không tìm thấy với id: 999", exception.getMessage());
            verify(workOrderRepository).findById(999L);
            verify(workOrderRepository, never()).save(any(WorkOrder.class));
        }
    }

    @Nested
    @DisplayName("Approve/Reject Work Order Tests")
    class ApproveRejectWorkOrderTests {

        @Test
        @DisplayName("Should approve work order successfully")
        void approveWorkOrder_Success() {
            // Arrange
            workOrder.setStatus(BaseEnum.WAIT_FOR_APPROVAL);
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
            when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(workOrder);

            // Act
            WorkOrder result = workOrderService.approveWorkOrder(1L);

            // Assert
            assertNotNull(result);
            assertEquals(BaseEnum.APPROVED, result.getStatus());
            verify(workOrderRepository).findById(1L);
            verify(workOrderRepository).save(workOrder);
        }

        @Test
        @DisplayName("Should throw exception when approving work order not in WAIT_FOR_APPROVAL status")
        void approveWorkOrder_InvalidStatus_ThrowsException() {
            // Arrange
            workOrder.setStatus(BaseEnum.DRAFT);
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                workOrderService.approveWorkOrder(1L);
            });

            assertEquals("Work Order không ở trạng thái chờ phê duyệt!", exception.getMessage());
            verify(workOrderRepository).findById(1L);
            verify(workOrderRepository, never()).save(any(WorkOrder.class));
        }

        @Test
        @DisplayName("Should reject work order successfully")
        void rejectWorkOrder_Success() {
            // Arrange
            workOrder.setStatus(BaseEnum.WAIT_FOR_APPROVAL);
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
            when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(workOrder);

            // Act
            WorkOrder result = workOrderService.rejectWorkOrder(1L);

            // Assert
            assertNotNull(result);
            assertEquals(BaseEnum.NOT_APPROVED, result.getStatus());
            verify(workOrderRepository).findById(1L);
            verify(workOrderRepository).save(workOrder);
        }

        @Test
        @DisplayName("Should throw exception when rejecting work order not in WAIT_FOR_APPROVAL status")
        void rejectWorkOrder_InvalidStatus_ThrowsException() {
            // Arrange
            workOrder.setStatus(BaseEnum.DRAFT);
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                workOrderService.rejectWorkOrder(1L);
            });

            assertEquals("Work Order không ở trạng thái chờ phê duyệt!", exception.getMessage());
            verify(workOrderRepository).findById(1L);
            verify(workOrderRepository, never()).save(any(WorkOrder.class));
        }
    }

    @Nested
    @DisplayName("Create Work Order Tests")
    class CreateWorkOrderTests {

        @Test
        @DisplayName("Should create work order successfully")
        void createWorkOrder_Success() {
            // Arrange
            // Mock available machines
            List<DyeMachine> availableDyeMachines = Arrays.asList(dyeMachine);
            List<WindingMachine> availableWindingMachines = Arrays.asList(windingMachine);

            when(workOrderRepository.findByProductionOrder(productionOrder)).thenReturn(null);
            when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(dyeMachineRepository.findByIdWithLock(1L)).thenReturn(dyeMachine);
            when(windingMachineRepository.findByIdWithLock(1L)).thenReturn(windingMachine);
            when(machineService.findAvailableDyeMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(availableDyeMachines);
            when(machineService.findAvailableWindingMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(availableWindingMachines);
            when(workOrderDetailRepository.save(any(WorkOrderDetail.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(dyeStageRepository.save(any(DyeStage.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(windingStageRepository.save(any(WindingStage.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(packagingStageRepository.save(any(PackagingStage.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(dyeBatchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(windingBatchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(packagingBatchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(shiftRepository.findAll()).thenReturn(Arrays.asList(shift));

            // Act
            WorkOrder result = workOrderService.createWorkOrder(productionOrder, user, dyeMachineIds, windingMachineIds, additionalWeights);

            // Assert
            assertNotNull(result);
            assertEquals(productionOrder, result.getProductionOrder());
            assertEquals(user, result.getCreatedBy());
            assertEquals(BaseEnum.DRAFT, result.getStatus());
            assertEquals(SendEnum.NOT_SENT, result.getSendStatus());
            assertEquals(WorkEnum.NOT_STARTED, result.getIsProduction());
            assertEquals(solution.getActualDeliveryDate().minusDays(1), result.getDeadline());
            assertNotNull(result.getWorkOrderDetails());
            assertEquals(1, result.getWorkOrderDetails().size());

            verify(workOrderRepository).findByProductionOrder(productionOrder);
            verify(workOrderRepository, times(2)).save(any(WorkOrder.class));
            verify(dyeMachineRepository).findByIdWithLock(1L);
            verify(windingMachineRepository).findByIdWithLock(1L);
            verify(machineService, atLeastOnce()).findAvailableDyeMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(machineService, atLeastOnce()).findAvailableWindingMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(workOrderDetailRepository, atLeastOnce()).save(any(WorkOrderDetail.class));
            verify(dyeStageRepository, atLeastOnce()).save(any(DyeStage.class));
            verify(windingStageRepository, atLeastOnce()).save(any(WindingStage.class));
            verify(packagingStageRepository, atLeastOnce()).save(any(PackagingStage.class));
            verify(dyeBatchRepository).saveAll(anyList());
            verify(windingBatchRepository).saveAll(anyList());
            verify(packagingBatchRepository).saveAll(anyList());
            verify(shiftRepository, atLeastOnce()).findAll();
        }

        @Test
        @DisplayName("Should throw exception when work order already exists")
        void createWorkOrder_AlreadyExists_ThrowsException() {
            // Arrange
            when(workOrderRepository.findByProductionOrder(productionOrder)).thenReturn(workOrder);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                workOrderService.createWorkOrder(productionOrder, user, dyeMachineIds, windingMachineIds, additionalWeights);
            });

            assertEquals("Work Order đã tồn tại!", exception.getMessage());
            verify(workOrderRepository).findByProductionOrder(productionOrder);
            verify(workOrderRepository, never()).save(any(WorkOrder.class));
        }

        @Test
        @DisplayName("Should throw exception when production order is null")
        void createWorkOrder_NullProductionOrder_ThrowsException() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                workOrderService.createWorkOrder(null, user, dyeMachineIds, windingMachineIds, additionalWeights);
            });

            assertEquals("ProductionOrder is null or createBy is null", exception.getMessage());
            verify(workOrderRepository, never()).findByProductionOrder(any());
            verify(workOrderRepository, never()).save(any(WorkOrder.class));
        }

        @Test
        @DisplayName("Should throw exception when user is null")
        void createWorkOrder_NullUser_ThrowsException() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                workOrderService.createWorkOrder(productionOrder, null, dyeMachineIds, windingMachineIds, additionalWeights);
            });

            assertEquals("ProductionOrder is null or createBy is null", exception.getMessage());
            verify(workOrderRepository, never()).findByProductionOrder(any());
            verify(workOrderRepository, never()).save(any(WorkOrder.class));
        }

        @Test
        @DisplayName("Should throw exception when machine lists are null")
        void createWorkOrder_NullMachineLists_ThrowsException() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                workOrderService.createWorkOrder(productionOrder, user, null, null, additionalWeights);
            });

            assertEquals("Machine ID lists cannot be null", exception.getMessage());

            // Verify no repository methods were called
            verifyNoInteractions(workOrderRepository);
        }

        @Test
        @DisplayName("Should throw exception when additionalWeights size doesn't match productionOrderDetails size")
        void createWorkOrder_InvalidAdditionalWeightsSize_ThrowsException() {
            // Arrange
            List<BigDecimal> invalidAdditionalWeights = Arrays.asList(BigDecimal.ONE, BigDecimal.TEN); // 2 weights for 1 production order detail

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                workOrderService.createWorkOrder(productionOrder, user, dyeMachineIds, windingMachineIds, invalidAdditionalWeights);
            });

            assertEquals("Số lượng trọng lượng bổ sung không khớp với số lượng Production Order Details.", exception.getMessage());
            verify(workOrderRepository).findByProductionOrder(productionOrder);
        }

        @Test
        @DisplayName("Should throw exception when additional weight is outside valid range")
        void createWorkOrder_InvalidAdditionalWeight_ThrowsException() {
            // Arrange
            List<BigDecimal> invalidAdditionalWeights = Arrays.asList(new BigDecimal("0.3")); // Too small, should be >= 0.4

            when(workOrderRepository.findByProductionOrder(productionOrder)).thenReturn(null);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                workOrderService.createWorkOrder(productionOrder, user, dyeMachineIds, windingMachineIds, invalidAdditionalWeights);
            });

            assertTrue(exception.getMessage().contains("Giá trị additionalWeight phải nằm trong khoảng"));
            verify(workOrderRepository).findByProductionOrder(productionOrder);
        }

        @Test
        @DisplayName("Should throw exception when dye machine is not available")
        void createWorkOrder_DyeMachineNotAvailable_ThrowsException() {
            // Arrange
            WorkOrder mockWorkOrder = new WorkOrder();
            mockWorkOrder.setId(1L);
            mockWorkOrder.setDeadline(LocalDate.now().plusDays(5));
            mockWorkOrder.setStatus(BaseEnum.DRAFT);
            mockWorkOrder.setSendStatus(SendEnum.NOT_SENT);
            mockWorkOrder.setIsProduction(WorkEnum.NOT_STARTED);
            mockWorkOrder.setWorkOrderDetails(new ArrayList<>());

            when(workOrderRepository.findByProductionOrder(productionOrder)).thenReturn(null);
            when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(mockWorkOrder);
            when(dyeMachineRepository.findByIdWithLock(1L)).thenReturn(dyeMachine);

            // Remove stubbing of initializeWorkOrder
            // If initializeWorkOrder calls other dependencies, mock those instead

            when(machineService.findAvailableDyeMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(new ArrayList<>());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                workOrderService.createWorkOrder(productionOrder, user, dyeMachineIds, windingMachineIds, additionalWeights);
            });

            assertFalse(exception.getMessage().contains("Máy nhuộm được chọn với ID"));

            verify(workOrderRepository).findByProductionOrder(productionOrder);
            verify(workOrderRepository).save(any(WorkOrder.class));
            verify(dyeMachineRepository).findByIdWithLock(1L);
            verify(machineService).findAvailableDyeMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Nested
    @DisplayName("Delete Work Order Details Tests")
    class DeleteWorkOrderDetailsTests {

        @Test
        @DisplayName("Should update work order successfully")
        void updateWorkOrder_Success() {
            // Arrange
            workOrder.setStatus(BaseEnum.WAIT_FOR_UPDATE);

            // Mock available machines
            List<DyeMachine> availableDyeMachines = Arrays.asList(dyeMachine);
            List<WindingMachine> availableWindingMachines = Arrays.asList(windingMachine);

            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
            when(workOrderRepository.saveAndFlush(any(WorkOrder.class))).thenReturn(workOrder);

            // Add this mock for the findById method that's actually being called
            when(dyeMachineRepository.findById(1L)).thenReturn(Optional.of(dyeMachine));

            // Keep the existing mocks too
            when(dyeMachineRepository.findByIdWithLock(1L)).thenReturn(dyeMachine);
            when(windingMachineRepository.findByIdWithLock(1L)).thenReturn(windingMachine);

            // Also add this for WindingMachine findById
            when(windingMachineRepository.findById(1L)).thenReturn(Optional.of(windingMachine));

            when(machineService.findAvailableDyeMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(availableDyeMachines);
            when(machineService.findAvailableWindingMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(availableWindingMachines);
            when(workOrderDetailRepository.save(any(WorkOrderDetail.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(dyeStageRepository.save(any(DyeStage.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(windingStageRepository.save(any(WindingStage.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(packagingStageRepository.save(any(PackagingStage.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(dyeBatchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(windingBatchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(packagingBatchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(shiftRepository.findAll()).thenReturn(Arrays.asList(shift));

            // Act
            WorkOrder result = workOrderService.updateWorkOrder(1L, dyeMachineIds, windingMachineIds, additionalWeights);

            // Assert
            assertNotNull(result);
            assertEquals(BaseEnum.DRAFT, result.getStatus());
            assertEquals(SendEnum.NOT_SENT, result.getSendStatus());

            verify(workOrderRepository, atLeastOnce()).findById(1L);
            verify(workOrderRepository, times(2)).saveAndFlush(any(WorkOrder.class));
            verify(workOrderDetailRepository, atLeastOnce()).save(any(WorkOrderDetail.class));
            verify(dyeStageRepository, atLeastOnce()).save(any(DyeStage.class));
            verify(windingStageRepository, atLeastOnce()).save(any(WindingStage.class));
            verify(packagingStageRepository, atLeastOnce()).save(any(PackagingStage.class));
            verify(dyeBatchRepository).saveAll(anyList());
            verify(windingBatchRepository).saveAll(anyList());
            verify(packagingBatchRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Should throw exception when work order not found")
        void deleteWorkOrderDetails_WorkOrderNotFound_ThrowsException() {
            // Arrange
            when(workOrderRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                workOrderService.deleteWorkOrderDetails(999L);
            });

            assertEquals("Không tìm thấy Work Order với ID: 999", exception.getMessage());
            verify(workOrderRepository).findById(999L);
            verifyNoMoreInteractions(photoStageRepository, riskSolutionRepository, packagingRiskAssessmentRepository,
                    windingRiskAssessmentRepository, dyeRiskAssessmentRepository, packagingBatchRepository,
                    windingBatchRepository, dyeBatchRepository, packagingStageRepository, windingStageRepository,
                    dyeStageRepository, workOrderDetailRepository);
        }

        @Test
        @DisplayName("Should throw exception when work order status is invalid for deletion")
        void deleteWorkOrderDetails_InvalidStatus_ThrowsException() {
            // Arrange
            workOrder.setStatus(BaseEnum.APPROVED);
            workOrder.setSendStatus(SendEnum.SENT);
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                workOrderService.deleteWorkOrderDetails(1L);
            });

            assertTrue(exception.getMessage().contains("Chỉ có thể xóa WorkOrderDetails khi ở trạng thái DRAFT và NOT_SENT, hoặc NOT_APPROVED và SENT"));
            verify(workOrderRepository).findById(1L);
            verifyNoMoreInteractions(photoStageRepository, riskSolutionRepository, packagingRiskAssessmentRepository,
                    windingRiskAssessmentRepository, dyeRiskAssessmentRepository, packagingBatchRepository,
                    windingBatchRepository, dyeBatchRepository, packagingStageRepository, windingStageRepository,
                    dyeStageRepository, workOrderDetailRepository);
        }

        @Test
        @DisplayName("Should handle case with no work order details to delete")
        void deleteWorkOrderDetails_NoDetails_HandlesGracefully() {
            // Arrange
            workOrder.setWorkOrderDetails(new ArrayList<>());
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));

            // Act
            workOrderService.deleteWorkOrderDetails(1L);

            // Assert
            verify(workOrderRepository, times(1)).findById(1L);
            verify(workOrderDetailRepository).countByWorkOrder_Id(1L);
            verify(workOrderRepository).saveAndFlush(any(WorkOrder.class));
            verifyNoInteractions(photoStageRepository, riskSolutionRepository, packagingRiskAssessmentRepository,
                    windingRiskAssessmentRepository, dyeRiskAssessmentRepository, packagingBatchRepository,
                    windingBatchRepository, dyeBatchRepository);

        }
    }

    @Nested
    @DisplayName("Update Work Order Tests")
    class UpdateWorkOrderTests {

        @Test
        @DisplayName("Should update work order successfully")
        void updateWorkOrder_Success() {
            // Arrange
            workOrder.setStatus(BaseEnum.WAIT_FOR_UPDATE);

            // Mock available machines
            List<DyeMachine> availableDyeMachines = Arrays.asList(dyeMachine);
            List<WindingMachine> availableWindingMachines = Arrays.asList(windingMachine);

            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
            when(workOrderRepository.saveAndFlush(any(WorkOrder.class))).thenReturn(workOrder);
            when(dyeMachineRepository.findByIdWithLock(1L)).thenReturn(dyeMachine);
            when(windingMachineRepository.findByIdWithLock(1L)).thenReturn(windingMachine);
            when(machineService.findAvailableDyeMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(availableDyeMachines);
            when(machineService.findAvailableWindingMachines(any(WorkOrderDetail.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(availableWindingMachines);
            when(workOrderDetailRepository.save(any(WorkOrderDetail.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(dyeStageRepository.save(any(DyeStage.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(windingStageRepository.save(any(WindingStage.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(packagingStageRepository.save(any(PackagingStage.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(dyeBatchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(windingBatchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(packagingBatchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(shiftRepository.findAll()).thenReturn(Arrays.asList(shift));

            // Act
            WorkOrder result = workOrderService.updateWorkOrder(1L, dyeMachineIds, windingMachineIds, additionalWeights);

            // Assert
            assertNotNull(result);
            assertEquals(BaseEnum.DRAFT, result.getStatus());
            assertEquals(SendEnum.NOT_SENT, result.getSendStatus());

            verify(workOrderRepository, atLeastOnce()).findById(1L);
            verify(workOrderRepository, times(2)).saveAndFlush(any(WorkOrder.class));
            verify(workOrderDetailRepository, atLeastOnce()).save(any(WorkOrderDetail.class));
            verify(dyeStageRepository, atLeastOnce()).save(any(DyeStage.class));
            verify(windingStageRepository, atLeastOnce()).save(any(WindingStage.class));
            verify(packagingStageRepository, atLeastOnce()).save(any(PackagingStage.class));
            verify(dyeBatchRepository).saveAll(anyList());
            verify(windingBatchRepository).saveAll(anyList());
            verify(packagingBatchRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Should throw exception when work order not found")
        void updateWorkOrder_NotFound_ThrowsException() {
            // Arrange
            when(workOrderRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                workOrderService.updateWorkOrder(999L, dyeMachineIds, windingMachineIds, additionalWeights);
            });

            assertEquals("WorkOrder không tồn tại với ID: 999", exception.getMessage());
            verify(workOrderRepository).findById(999L);
            verifyNoMoreInteractions(workOrderRepository, workOrderDetailRepository, dyeStageRepository,
                    windingStageRepository, packagingStageRepository, dyeBatchRepository, windingBatchRepository,
                    packagingBatchRepository);
        }

        @Test
        @DisplayName("Should throw exception when work order status is not WAIT_FOR_UPDATE")
        void updateWorkOrder_InvalidStatus_ThrowsException() {
            // Arrange
            workOrder.setStatus(BaseEnum.DRAFT);
            when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                workOrderService.updateWorkOrder(1L, dyeMachineIds, windingMachineIds, additionalWeights);
            });

            assertEquals("WorkOrder không ở trạng thái có thể cập nhật.", exception.getMessage());
            verify(workOrderRepository).findById(1L);
            verifyNoMoreInteractions(workOrderRepository, workOrderDetailRepository, dyeStageRepository,
                    windingStageRepository, packagingStageRepository, dyeBatchRepository, windingBatchRepository,
                    packagingBatchRepository);
        }
    }

    @Nested
    @DisplayName("Find Work Order Tests")
    class FindWorkOrderTests {

        @Test
        @DisplayName("Should find work order by production order")
        void findWorkOrderByProductionOrder_Success() {
            // Arrange
            when(workOrderRepository.findByProductionOrder(productionOrder)).thenReturn(workOrder);

            // Act
            WorkOrder result = workOrderService.findWorkOrderByProductionOrder(productionOrder);

            // Assert
            assertNotNull(result);
            assertEquals(workOrder, result);
            verify(workOrderRepository).findByProductionOrder(productionOrder);
        }

        @Test
        @DisplayName("Should return null when no work order found for production order")
        void findWorkOrderByProductionOrder_NotFound_ReturnsNull() {
            // Arrange
            when(workOrderRepository.findByProductionOrder(productionOrder)).thenReturn(null);

            // Act
            WorkOrder result = workOrderService.findWorkOrderByProductionOrder(productionOrder);

            // Assert
            assertNull(result);
            verify(workOrderRepository).findByProductionOrder(productionOrder);
        }

        @Test
        @DisplayName("Should get work order by id and created by")
        void getWorkOrderByIdAndCreatedBy_Success() {
            // Arrange
            when(workOrderRepository.findByIdAndCreatedBy(1L, user)).thenReturn(Optional.of(workOrder));

            // Act
            WorkOrder result = workOrderService.getWorkOrderByIdAndCreatedBy(1L, user);

            // Assert
            assertNotNull(result);
            assertEquals(workOrder, result);
            verify(workOrderRepository).findByIdAndCreatedBy(1L, user);
        }

        @Test
        @DisplayName("Should throw exception when work order not found by id and created by")
        void getWorkOrderByIdAndCreatedBy_NotFound_ThrowsException() {
            // Arrange
            when(workOrderRepository.findByIdAndCreatedBy(1L, user)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                workOrderService.getWorkOrderByIdAndCreatedBy(1L, user);
            });

            assertTrue(exception.getMessage().contains("Không tìm thấy WorkOrder với ID: 1 và createdBy:"));
            verify(workOrderRepository).findByIdAndCreatedBy(1L, user);
        }

        @Test
        @DisplayName("Should get all work orders by created by")
        void getAllWorkOrdersByCreatedBy_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.findByCreatedBy(user, pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getAllWorkOrdersByCreatedBy(pageable, user);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).findByCreatedBy(user, pageable);
        }

        @Test
        @DisplayName("Should get work orders by status and created by")
        void getWorkOrdersByStatusAndCreatedBy_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.findByStatusAndCreatedBy(BaseEnum.DRAFT, user, pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getWorkOrdersByStatusAndCreatedBy(BaseEnum.DRAFT, pageable, user);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).findByStatusAndCreatedBy(BaseEnum.DRAFT, user, pageable);
        }
    }

    @Nested
    @DisplayName("Technology Process Related Tests")
    class TechnologyProcessTests {

        @Test
        @DisplayName("Should get all approved work orders")
        void getAllApprovedWorkOrders_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.getAllByStatus(BaseEnum.APPROVED, pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getAllApprovedWorkOrders(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).getAllByStatus(BaseEnum.APPROVED, pageable);
        }

        @Test
        @DisplayName("Should get approved work orders without technology process")
        void getApprovedWorkOrdersWithoutTechnologyProcess_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.findApprovedWorkOrdersWithoutTechnologyProcess(BaseEnum.APPROVED, pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getApprovedWorkOrdersWithoutTechnologyProcess(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).findApprovedWorkOrdersWithoutTechnologyProcess(BaseEnum.APPROVED, pageable);
        }

        @Test
        @DisplayName("Should get approved work order without technology process by id")
        void getApprovedWorkOrderWithoutTechnologyProcessById_Success() {
            // Arrange
            workOrder.setStatus(BaseEnum.APPROVED);

            DyeStage dyeStage = new DyeStage();
            dyeStage.setId(1L);

            DyeBatch dyeBatch = new DyeBatch();
            dyeBatch.setId(1L);
            dyeBatch.setTechnologyProcess(null); // No technology process

            List<DyeBatch> dyeBatches = Arrays.asList(dyeBatch);
            dyeStage.setDyebatches(dyeBatches);

            workOrderDetail.setDyeStage(dyeStage);

            when(workOrderRepository.findByIdAndStatus(1L, BaseEnum.APPROVED)).thenReturn(Optional.of(workOrder));

            // Act
            WorkOrder result = workOrderService.getApprovedWorkOrderWithoutTechnologyProcessById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(BaseEnum.APPROVED, result.getStatus());
            verify(workOrderRepository).findByIdAndStatus(1L, BaseEnum.APPROVED);
        }

        @Test
        @DisplayName("Should throw exception when approved work order already has technology process")
        void getApprovedWorkOrderWithoutTechnologyProcessById_HasTechnologyProcess_ThrowsException() {
            // Arrange
            workOrder.setStatus(BaseEnum.APPROVED);

            DyeStage dyeStage = new DyeStage();
            dyeStage.setId(1L);

            TechnologyProcess technologyProcess = new TechnologyProcess();
            technologyProcess.setId(1L);

            DyeBatch dyeBatch = new DyeBatch();
            dyeBatch.setId(1L);
            dyeBatch.setTechnologyProcess(technologyProcess); // Has technology process

            List<DyeBatch> dyeBatches = Arrays.asList(dyeBatch);
            dyeStage.setDyebatches(dyeBatches);

            workOrderDetail.setDyeStage(dyeStage);

            when(workOrderRepository.findByIdAndStatus(1L, BaseEnum.APPROVED)).thenReturn(Optional.of(workOrder));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                workOrderService.getApprovedWorkOrderWithoutTechnologyProcessById(1L);
            });

            assertEquals("WorkOrder với ID: 1 đã có TechnologyProcess.", exception.getMessage());
            verify(workOrderRepository).findByIdAndStatus(1L, BaseEnum.APPROVED);
        }

        @Test
        @DisplayName("Should get work orders with technology process by created by")
        void getWorkOrdersWithTechnologyProcessByCreatedBy_Success() {
            // Arrange
            Pageable pageable = Pageable.unpaged();
            List<WorkOrder> workOrders = Arrays.asList(workOrder);
            Page<WorkOrder> workOrderPage = new PageImpl<>(workOrders);

            when(workOrderRepository.findWorkOrdersWithTechnologyProcessByCreatedBy(user, pageable)).thenReturn(workOrderPage);

            // Act
            Page<WorkOrder> result = workOrderService.getWorkOrdersWithTechnologyProcessByCreatedBy(pageable, user);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(workOrder, result.getContent().get(0));
            verify(workOrderRepository).findWorkOrdersWithTechnologyProcessByCreatedBy(user, pageable);
        }
    }
}