package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.*;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.models.entities.Thread;
import fpt.g36.gapms.repositories.*;
import fpt.g36.gapms.services.MachineService;
import fpt.g36.gapms.services.WorkOrderService;
import jakarta.persistence.EntityManager;

import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {
    @Autowired
    private WorkOrderRepository workOrderRepository;
    @Autowired
    private MachineService machineService;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private DyeMachineRepository dyeMachineRepository;
    @Autowired
    private WindingMachineRepository windingMachineRepository;
    @Autowired
    private WorkOrderDetailsRepository workOrderDetailRepository;
    @Autowired
    private DyeStageRepository dyeStageRepository;
    @Autowired
    private WindingStageRepository windingStageRepository;
    @Autowired
    private PackagingStageRepository packagingStageRepository;
    @Autowired
    private DyeBatchRepository dyeBatchRepository;
    @Autowired
    private DyeRiskAssessmentRepository dyeRiskAssessmentRepository;
    @Autowired
    private WindingRiskAssessmentRepository windingRiskAssessmentRepository;
    @Autowired
    private PackagingRiskAssessmentRepository packagingRiskAssessmentRepository;
    @Autowired
    private WindingBatchRepository windingBatchRepository;
    @Autowired
    private PackagingBatchRepository packagingBatchRepository;
    @Autowired
    private RiskSolutionRepository riskSolutionRepository;
    @Autowired
    private PhotoStageRepository photoStageRepository;
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Page<WorkOrder> getAllWorkOrderTeamLeader(Pageable pageable, String workOrderId) {
        Long id = null;
        if (workOrderId != null && !workOrderId.trim().isEmpty()) {
            try {
                String numericPart = workOrderId.replace("WO-", "").trim();
                id = Long.parseLong(numericPart);
            } catch (NumberFormatException e) {
                id = null;
            }
        }
        return workOrderRepository.getAllWorkOrderTeamLeader(id, pageable);
    }

    @Override
    public Page<WorkOrder> getAllWorkOrders(Pageable pageable) {
        return workOrderRepository.findAllByOrderByCreateAt(pageable);
    }

    @Override
    public Page<WorkOrder> getAllSubmittedWorkOrders(Pageable pageable) {
        return workOrderRepository.findAllBySendStatus(SendEnum.SENT, pageable);
    }

    @Override
    public WorkOrder getSubmittedWorkOrderById(Long id) {
        return workOrderRepository.findByIdAndSendStatus(id, SendEnum.SENT);
    }

    @Transactional
    @Override
    public WorkOrder getWorkOrderById(Long id) {
        return workOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy Work Order với ID: " + id));
    }

    @Override
    public WorkOrder getWorkOrderByProductionOrder(ProductionOrder productionOrder) {
        return workOrderRepository.findByProductionOrder(productionOrder);
    }

    @Override
    public Page<WorkOrder> getWorkOrdersByStatus(BaseEnum status, Pageable pageable) {
        return workOrderRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<WorkOrder> getSubmittedWorkOrdersByStatus(BaseEnum status, Pageable pageable) {
        return workOrderRepository.findByStatusAndSendStatus(status, SendEnum.SENT, pageable);
    }

    @Override
    public WorkOrder submitWorkOrder(Long workOrderId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work Order không tìm thấy với id: " + workOrderId));
        if (workOrder.getSendStatus() == SendEnum.SENT) {
            throw new RuntimeException("Work Order đã được gửi trước đó.");
        }
        workOrder.setSendStatus(SendEnum.SENT);
        workOrder.setStatus(BaseEnum.WAIT_FOR_APPROVAL);
        return workOrderRepository.save(workOrder);
    }

    public WorkOrder approveWorkOrder(Long id) {
        WorkOrder workOrder = getWorkOrderById(id);
        if (workOrder.getStatus() != BaseEnum.WAIT_FOR_APPROVAL) {
            throw new RuntimeException("Work Order không ở trạng thái chờ phê duyệt!");
        }
        workOrder.setStatus(BaseEnum.APPROVED);
        workOrder.setUpdateAt(LocalDateTime.now());
        return workOrderRepository.save(workOrder);
    }

    public WorkOrder rejectWorkOrder(Long id) {
        WorkOrder workOrder = getWorkOrderById(id);
        if (workOrder.getStatus() != BaseEnum.WAIT_FOR_APPROVAL) {
            throw new RuntimeException("Work Order không ở trạng thái chờ phê duyệt!");
        }
        workOrder.setStatus(BaseEnum.NOT_APPROVED);
        workOrder.setUpdateAt(LocalDateTime.now());
        return workOrderRepository.save(workOrder);
    }

    @Override
    @Transactional
    public WorkOrder createWorkOrder(ProductionOrder productionOrder,
                                     User createBy,
                                     List<Long> selectedDyeMachineIds,
                                     List<Long> selectedWindingMachineIds,
                                     List<BigDecimal> additionalWeights) {
        try {
            // Validate các input
            validateInput(productionOrder, createBy, selectedDyeMachineIds, selectedWindingMachineIds, additionalWeights);

            // Kiểm tra lại xem production order đã có work order hay chưa
            WorkOrder existingWorkOrder = findWorkOrderByProductionOrder(productionOrder);
            if (existingWorkOrder != null) {
                System.err.println("Error: Work Order đã tồn tại cho ProductionOrder ID: " + productionOrder.getId());
                throw new IllegalArgumentException("Work Order đã tồn tại!");
            }

            // Khởi tạo và lưu work order trước
            WorkOrder newWorkOrder = initializeWorkOrder(productionOrder, createBy);
            newWorkOrder = workOrderRepository.save(newWorkOrder);

            // Kiểm tra tính khả dụng của các máy trước khi tạo WorkOrderDetails
            LocalDateTime plannedStartAt = LocalDateTime.now().plusHours(2);
            LocalDateTime plannedEndAt = newWorkOrder.getDeadline().atTime(LocalTime.MAX);

            // Khóa các máy được chọn để tránh đồng thời
            List<DyeMachine> lockedDyeMachines = new ArrayList<>();
            List<WindingMachine> lockedWindingMachines = new ArrayList<>();

            // Khóa máy nhuộm
            for (Long dyeMachineId : selectedDyeMachineIds) {
                DyeMachine dyeMachine = dyeMachineRepository.findByIdWithLock(dyeMachineId);
                if (dyeMachine == null) {
                    throw new IllegalArgumentException("Không tìm thấy Dye Machine ID " + dyeMachineId);
                }
                lockedDyeMachines.add(dyeMachine);
            }

            // Khóa máy cuốn
            for (Long windingMachineId : selectedWindingMachineIds) {
                WindingMachine windingMachine = windingMachineRepository.findByIdWithLock(windingMachineId);
                if (windingMachine == null) {
                    throw new IllegalArgumentException("Không tìm thấy Winding Machine ID " + windingMachineId);
                }
                lockedWindingMachines.add(windingMachine);
            }

            // Tạo một WorkOrderDetail tạm thời để kiểm tra tính khả dụng
            for (int i = 0; i < productionOrder.getProductionOrderDetails().size(); i++) {
                WorkOrderDetail tempWorkOrderDetail = new WorkOrderDetail();
                tempWorkOrderDetail.setWorkOrder(newWorkOrder);
                tempWorkOrderDetail.setProductionOrderDetail(productionOrder.getProductionOrderDetails().get(i));
                tempWorkOrderDetail.setPlannedStartAt(plannedStartAt);
                tempWorkOrderDetail.setPlannedEndAt(plannedEndAt);

                // Kiểm tra máy nhuộm
                if (i < selectedDyeMachineIds.size()) {
                    Long dyeMachineId = selectedDyeMachineIds.get(i);
                    List<DyeMachine> availableDyeMachines = machineService.findAvailableDyeMachines(tempWorkOrderDetail, plannedStartAt, plannedEndAt);

                    //Quét trong list máy nhuộm khả dụng xem có rảnh thật khôgn
                    if (availableDyeMachines.stream().noneMatch(m -> m.getId().equals(dyeMachineId))) {
                        System.err.println("Lỗi: Máy nhuộm được chọn với ID " + dyeMachineId
                                + " không khả dụng cho ProductionOrderDetail ID: "
                                + tempWorkOrderDetail.getProductionOrderDetail().getId());
                        throw new IllegalArgumentException("Máy nhuộm được chọn với ID "
                                + dyeMachineId + " không khả dụng");
                    }
                }

                // Kiểm tra máy cuốn
                if (i < selectedWindingMachineIds.size()) {
                    Long windingMachineId = selectedWindingMachineIds.get(i);
                    List<WindingMachine> availableWindingMachines = machineService.findAvailableWindingMachines(tempWorkOrderDetail, plannedStartAt, plannedEndAt);
                    if (availableWindingMachines.stream().noneMatch(m -> m.getId().equals(windingMachineId))) {
                        System.err.println("Lỗi: Máy cuốn được chọn với ID " + windingMachineId
                                + " không khả dụng cho ProductionOrderDetail ID: "
                                + tempWorkOrderDetail.getProductionOrderDetail().getId());
                        throw new IllegalArgumentException("Máy cuốn được chọn với ID "
                                + windingMachineId + " không khả dụng");
                    }
                }
            }

            // Nếu tất cả máy đều khả dụng, tiến hành thêm WorkOrderDetails
            List<WorkOrderDetail> workOrderDetails = addWorkOrderDetails(newWorkOrder, productionOrder, selectedDyeMachineIds, selectedWindingMachineIds, additionalWeights);
            newWorkOrder.setWorkOrderDetails(workOrderDetails);
            return workOrderRepository.save(newWorkOrder);
        } catch (Exception e) {
            System.err.println("Error creating WorkOrder for ProductionOrder ID: " + (productionOrder != null ? productionOrder.getId() : "null") + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Validates the input parameters for creating/updating a WorkOrder.
     */
    private void validateInput(ProductionOrder productionOrder,
                               User createBy,
                               List<Long> dyeMachineIds,
                               List<Long> windingMachineIds,
                               List<BigDecimal> additionalWeights) {
        if (productionOrder == null || createBy == null) {
            System.err.println("Error: ProductionOrder or createBy is null");
            throw new IllegalArgumentException("ProductionOrder is null or createBy is null");
        }
        if (productionOrder.getPurchaseOrder() == null || productionOrder.getPurchaseOrder().getSolution() == null || productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate() == null) {
            System.err.println("Error: ActualDeliveryDate is null in ProductionOrder ID: " + (productionOrder != null ? productionOrder.getId() : "null"));
            throw new IllegalArgumentException("ActualDeliveryDate is null");
        }
        if (dyeMachineIds == null || windingMachineIds == null) {
            System.err.println("Error: DyeMachineIds or WindingMachineIds list is null");
            throw new IllegalArgumentException("Machine ID lists cannot be null");
        }

        // Kiểm tra số lượng additionalWeights và tải trước Thread để tránh lazy loading
        if (additionalWeights == null || additionalWeights.size() != productionOrder.getProductionOrderDetails().size()) {
            throw new IllegalArgumentException("Số lượng trọng lượng bổ sung không khớp với số lượng Production Order Details.");
        }
        for (ProductionOrderDetail detail : productionOrder.getProductionOrderDetails()) {
            if (detail.getPurchaseOrderDetail() != null && detail.getPurchaseOrderDetail().getProduct() != null) {
                Thread thread = detail.getPurchaseOrderDetail().getProduct().getThread(); // Force lazy loading within transaction
                if (thread != null) {
                    thread.getConvert_rate(); // Đảm bảo dữ liệu được tải
                }
            }
        }

    }

    /**
     * Initializes a new WorkOrder with basic properties.
     */
    private WorkOrder initializeWorkOrder(ProductionOrder productionOrder, User createBy) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setCreateAt(LocalDateTime.now());
        workOrder.setCreatedBy(createBy);
        workOrder.setProductionOrder(productionOrder);
        workOrder.setUpdateAt(LocalDateTime.now());
        workOrder.setDeadline(productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate().minusDays(1));
        workOrder.setStatus(BaseEnum.DRAFT);
        workOrder.setSendStatus(SendEnum.NOT_SENT);
        workOrder.setIsProduction(WorkEnum.NOT_STARTED);
        workOrder.setWorkOrderDetails(new ArrayList<>());
        System.err.println("Đã khởi tạo WorkOrder cho ProductionOrder ID: " + productionOrder.getId());
        return workOrder;
    }

    /**
     * Adds WorkOrderDetails to the WorkOrder based on ProductionOrderDetails.
     */
    private List<WorkOrderDetail> addWorkOrderDetails(WorkOrder workOrder, ProductionOrder productionOrder, List<Long> dyeMachineIds, List<Long> windingMachineIds, List<BigDecimal> additionalWeights) {
        List<WorkOrderDetail> workOrderDetails = new ArrayList<>();
        for (int i = 0; i < productionOrder.getProductionOrderDetails().size(); i++) {
            try {
                ProductionOrderDetail detail = productionOrder.getProductionOrderDetails().get(i);
                WorkOrderDetail workOrderDetail = initializeWorkOrderDetail(workOrder, detail);
                workOrderDetail.setAdditionalWeight(additionalWeights.get(i));

                // Lưu WorkOrderDetail trước để đảm bảo có ID
                workOrderDetail = workOrderDetailRepository.save(workOrderDetail);

                // Gán DyeStage
                DyeStage dyeStage = addDyeStage(workOrderDetail, dyeMachineIds, i, additionalWeights.get(i));
                workOrderDetail.setDyeStage(dyeStage);

                // Gán WindingStage
                WindingStage windingStage = addWindingStage(workOrderDetail, windingMachineIds, i);
                windingStage = windingStageRepository.save(windingStage);
                workOrderDetail.setWindingStage(windingStage);

                // Gán PackagingStage
                PackagingStage packagingStage = addPackagingStage(workOrderDetail);
                packagingStage = packagingStageRepository.save(packagingStage);
                workOrderDetail.setPackagingStage(packagingStage);

                // Lưu lại WorkOrderDetail sau khi cập nhật các stage
                workOrderDetail = workOrderDetailRepository.save(workOrderDetail);
                workOrder.getWorkOrderDetails().add(workOrderDetail);

                System.err.println("Đã thêm WorkOrderDetail: " + workOrderDetail.getId() + " cho ProductionOrderDetail ID: " + detail.getId() + " và PurchaseOrderDetail ID: " + workOrderDetail.getPurchaseOrderDetail().getId());
            } catch (Exception e) {
                System.err.println("Lỗi khi tạo WorkOrderDetail cho ProductionOrderDetail ID: " + productionOrder.getProductionOrderDetails().get(i).getId() + " - " + e.getMessage());
                throw e;
            }
        }
        return workOrderDetails;
    }

    /**
     * Initializes a WorkOrderDetail with basic properties.
     */
    private WorkOrderDetail initializeWorkOrderDetail(WorkOrder workOrder, ProductionOrderDetail productionOrderDetail) {
        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setCreateAt(LocalDateTime.now());
        detail.setWorkOrder(workOrder);
        detail.setProductionOrderDetail(productionOrderDetail);
        detail.setPurchaseOrderDetail(productionOrderDetail.getPurchaseOrderDetail());
        detail.setPlannedStartAt(LocalDateTime.now().plusHours(2));
        detail.setPlannedEndAt(workOrder.getDeadline().atTime(LocalTime.MAX));
        detail.setWorkStatus(WorkEnum.NOT_STARTED);
        System.err.println("Đã khởi tạo WorkOrderDetail cho ProductionOrderDetail ID: " + productionOrderDetail.getId());
        return detail;
    }

    /**
     * Adds a DyeStage to the WorkOrderDetail.
     */
    private DyeStage addDyeStage(WorkOrderDetail workOrderDetail,
                                 List<Long> dyeMachineIds,
                                 int dyeMachineIndex,
                                 BigDecimal additionalWeight) {
        // Khởi tạo thông tin cơ bản cho stage nhuộm
        DyeStage dyeStage = initializeDyeStage(workOrderDetail);

        // Lấy thông tin cho máy nhuộm
        DyeMachine dyeMachine = selectDyeMachine(workOrderDetail, dyeMachineIds, dyeMachineIndex);

        // Lấy các thông tin cho stage nhuộm sau khi tính toán
        BigDecimal[] dyeCalculations = calculateDyeStageValues(workOrderDetail, dyeMachine, additionalWeight);
        BigDecimal coneWeight = dyeCalculations[0];
        BigDecimal coneBatchWeight = dyeCalculations[1];
        int dyeBatches = dyeCalculations[2].intValue();
        BigDecimal littersBatch = dyeCalculations[3];
        BigDecimal coneBatchQuantity = dyeCalculations[4];
        BigDecimal coneQuantity = dyeCalculations[5];
        BigDecimal litters = dyeCalculations[6];

        // Validate stage nhuộm
        validateDyeStage(dyeMachine, littersBatch, coneBatchQuantity, workOrderDetail.getProductionOrderDetail().getId());

        dyeStage.setCone_weight(coneWeight);
        dyeStage.setCone_quantity(coneQuantity);
        dyeStage.setLiters(litters);
        dyeStage.setCone_batch_weight(coneBatchWeight);

        // Tính toán deadline cho quá trình nhuộm
        LocalDateTime dyeDeadline = calculateDyeDeadline(dyeStage.getPlannedStart(), dyeBatches, workOrderDetail);

        validateDeadline(dyeDeadline, workOrderDetail.getWorkOrder().getDeadline(), "Dye Stage", workOrderDetail.getProductionOrderDetail().getId());

        dyeStage.setDeadline(dyeDeadline);
        dyeStage.setDyeMachine(dyeMachine);

        // Lưu DyeStage trước khi tạo và lưu DyeBatch
        dyeStage = dyeStageRepository.save(dyeStage);

        // Tạo và lưu dye batches
        List<DyeBatch> dyeBatchList = createDyeBatches(dyeStage, dyeMachine, dyeBatches, coneWeight, coneBatchWeight);
        dyeBatchList = dyeBatchRepository.saveAll(dyeBatchList);
        dyeStage.setDyebatches(dyeBatchList);

        // Gán team leaders và QA
        assignTeamLeadersAndQAForDyeStage(dyeStage);

        // Lưu lại DyeStage sau khi cập nhật danh sách DyeBatch
        dyeStage = dyeStageRepository.save(dyeStage);

        System.err.println("Đã thêm DyeStage cho WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        return dyeStage;
    }

    /**
     * Initializes a DyeStage with basic properties.
     */
    private DyeStage initializeDyeStage(WorkOrderDetail workOrderDetail) {
        DyeStage dyeStage = new DyeStage();
        dyeStage.setCreateAt(LocalDateTime.now());
        dyeStage.setWorkStatus(WorkEnum.NOT_STARTED);
        dyeStage.setWorkOrderDetail(workOrderDetail);
        dyeStage.setPlannedStart(LocalDateTime.now().plusHours(2));
        System.err.println("Initialized DyeStage for WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        return dyeStage;
    }

    /**
     * Selects a DyeMachine from the available list.
     */
    private DyeMachine selectDyeMachine(WorkOrderDetail workOrderDetail, List<Long> dyeMachineIds, int index) {
        if (index >= dyeMachineIds.size()) {
            System.err.println("Lỗi: Không đủ máy nhuộm được chọn cho ProductionOrderDetail ID: "
                    + workOrderDetail.getProductionOrderDetail().getId());
            throw new IllegalArgumentException("Không đủ máy nhuộm được chọn cho tất cả WorkOrderDetails");
        }
        Long dyeMachineId = dyeMachineIds.get(index);
        DyeMachine selectedMachine = dyeMachineRepository.findById(dyeMachineId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy máy nhuộm với ID: " + dyeMachineId));
        System.err.println("Đã chọn máy nhuộm với ID: " + dyeMachineId + " cho ProductionOrderDetail ID: "
                + workOrderDetail.getProductionOrderDetail().getId());
        return selectedMachine;
    }

    /**
     * Calculates values for DyeStage (coneWeight, coneBatchWeight, dyeBatches, etc.).
     * Returns an array: [coneWeight, coneBatchWeight, dyeBatches, littersBatch, coneBatchQuantity, coneQuantity, litters]
     */
    private BigDecimal[] calculateDyeStageValues(WorkOrderDetail workOrderDetail,
                                                 DyeMachine dyeMachine,
                                                 BigDecimal additionalWeight) {
        // Lấy khối lượng dự kiến được tính trước ở Production Order
        BigDecimal threadMass = workOrderDetail.getProductionOrderDetail().getThread_mass();

        //Nếu không tồn tại hoặc bị lỗi thì tự tính lại
        if (threadMass == null || threadMass.equals(BigDecimal.ZERO)) {
            threadMass = workOrderDetail.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate()
                    .multiply(BigDecimal.valueOf(workOrderDetail.getPurchaseOrderDetail().getQuantity()));
            System.err.println("ThreadMass trong production order bị lỗi hoặc bằng 0, đã tính lại: " + threadMass
                    + " cho WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        }

        // Kiểm tra giới hạn của additionalWeight
        BigDecimal minAdditionalWeight = BigDecimal.valueOf(0.4);
        BigDecimal maxAdditionalWeight = threadMass.divide(BigDecimal.valueOf(10), 2, BigDecimal.ROUND_DOWN);

        if (additionalWeight.compareTo(minAdditionalWeight) < 0 || additionalWeight.compareTo(maxAdditionalWeight) > 0) {
            throw new IllegalArgumentException("Giá trị additionalWeight phải nằm trong khoảng ["
                    + minAdditionalWeight + ", " + maxAdditionalWeight + "] cho WorkOrderDetail ID: "
                    + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        }

        // Tính coneWeight với additionalWeight do người dùng nhập
        BigDecimal coneWeight = threadMass.add(additionalWeight);
        BigDecimal maxWeight = dyeMachine.getMaxWeight();
        BigDecimal convertRate = workOrderDetail.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate();

        BigDecimal coneBatchWeight;
        BigDecimal dyeBatches;

        // Chia ra từng trường hợp để xử lý cho tính toán số mẻ
        if (maxWeight.compareTo(coneWeight) >= 0) {
            dyeBatches = BigDecimal.ONE;
            coneBatchWeight = coneWeight;
        } else {
            BigDecimal maxProductPerBatch = maxWeight.divide(convertRate, 2, BigDecimal.ROUND_DOWN);
            int maxProductInt = maxProductPerBatch.intValue();
            coneBatchWeight = convertRate.multiply(BigDecimal.valueOf(maxProductInt));
            dyeBatches = coneWeight.divide(coneBatchWeight, 2, BigDecimal.ROUND_UP);
        }

        BigDecimal littersBatch = coneBatchWeight.multiply(BigDecimal.valueOf(6));
        BigDecimal coneBatchQuantity = coneBatchWeight.divide(BigDecimal.valueOf(1.25), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal coneQuantity = coneWeight.divide(BigDecimal.valueOf(1.25), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal litters = coneWeight.multiply(BigDecimal.valueOf(6));

        System.err.println("Đã tính toán giá trị DyeStage cho WorkOrderDetail ID: "
                + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null")
                + " - coneWeight: " + coneWeight
                + ", coneBatchWeight: " + coneBatchWeight
                + ", dyeBatches: " + dyeBatches);
        return new BigDecimal[]{coneWeight, coneBatchWeight, dyeBatches,
                littersBatch, coneBatchQuantity, coneQuantity, litters};
    }

    /**
     * Validates DyeStage constraints (littersBatch and coneBatchQuantity).
     */
    private void validateDyeStage(DyeMachine dyeMachine,
                                  BigDecimal littersBatch,
                                  BigDecimal coneBatchQuantity,
                                  Long detailId) {
        //
        BigDecimal littersMin = dyeMachine.getLittersMin();
        BigDecimal littersMax = dyeMachine.getLittersMax();
        BigDecimal coneMin = dyeMachine.getConeMin();
        BigDecimal coneMax = dyeMachine.getConeMax();

        //
        boolean isLittersBatchValid = littersBatch.compareTo(littersMin) >= 0 && littersBatch.compareTo(littersMax) <= 0;
        boolean isConeBatchQuantityValid = coneBatchQuantity.compareTo(coneMin) >= 0 && coneBatchQuantity.compareTo(coneMax) <= 0;

        //
        if (!isLittersBatchValid || !isConeBatchQuantityValid) {
            if (!isLittersBatchValid) {
                System.err.println("Lỗi: Litters Batch (" + littersBatch + ") " +
                        "không nằm trong khoảng [" + littersMin + ", " + littersMax + "] " +
                        "cho ProductionOrderDetail ID: " + detailId);
            }
            if (!isConeBatchQuantityValid) {
                System.err.println("Lỗi: Cone Batch Quantity (" + coneBatchQuantity + ") " +
                        "không nằm trong khoảng [" + coneMin + ", " + coneMax + "] " +
                        "cho ProductionOrderDetail ID: " + detailId);

            }
            throw new IllegalStateException("Máy nhuộm không hợp lệ cho ProductionOrderDetail ID: " + detailId);
        }
    }

    /**
     * Calculates the deadline for DyeStage.
     */
    private LocalDateTime calculateDyeDeadline(LocalDateTime plannedStart,
                                               int dyeBatches,
                                               WorkOrderDetail workOrderDetail) {
        //
        long dyeDurationMinutes = dyeBatches * 120 + (dyeBatches - 1) * 15;
        LocalDateTime deadline = plannedStart.plusMinutes(dyeDurationMinutes);

        // Lấy actualDeliveryDate từ ProductionOrder
        LocalDate actualDeliveryDate = workOrderDetail.getWorkOrder().getProductionOrder()
                .getPurchaseOrder().getSolution().getActualDeliveryDate();
        LocalDateTime actualDeliveryDateTime = actualDeliveryDate.atTime(LocalTime.MAX);

        // Cộng thêm 1 ngày vào deadline
        LocalDateTime adjustedDeadline = deadline.plusDays(1);

        // Kiểm tra nếu deadline sau khi cộng vượt quá actualDeliveryDate thì không cộng
        if (adjustedDeadline.isAfter(actualDeliveryDateTime)) {
            System.err.println("Deadline của DyeStage không được cộng thêm 1 ngày vì vượt quá actualDeliveryDate: "
                    + actualDeliveryDate + " cho WorkOrderDetail ID: "
                    + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        } else {
            deadline = adjustedDeadline;
        }

        System.err.println("Đã tính toán thời hạn DyeStage: " + deadline + " với " + dyeBatches + " lô");
        return deadline;
    }

    /**
     * Validates that a deadline does not exceed the WorkOrder deadline.
     */
    private void validateDeadline(LocalDateTime stageDeadline,
                                  LocalDate workOrderDeadline,
                                  String stageName,
                                  Long detailId) {
        if (stageDeadline.isAfter(workOrderDeadline.atTime(LocalTime.MAX))) {
            System.err.println("Lỗi: Thời hạn của " + stageName + " " + stageDeadline
                    + " vượt quá thời hạn WorkOrder " + workOrderDeadline
                    + " cho ProductionOrderDetail ID: " + detailId);
            throw new IllegalStateException("Thời hạn của " + stageName
                    + " vượt quá ngày giao hàng thực tế cho chi tiết " + detailId);

        }
    }

    /**
     * Creates DyeBatches for the DyeStage.
     */
    private List<DyeBatch> createDyeBatches(DyeStage dyeStage,
                                            DyeMachine dyeMachine,
                                            int dyeBatches,
                                            BigDecimal coneWeight,
                                            BigDecimal coneBatchWeight) {
        List<DyeBatch> dyeBatchList = new ArrayList<>();
        BigDecimal remainingConeWeight = coneWeight;
        for (int i = 0; i < dyeBatches; i++) {
            DyeBatch dyeBatch = new DyeBatch();
            dyeBatch.setWorkStatus(WorkEnum.NOT_STARTED);
            dyeBatch.setTestStatus(TestEnum.NOT_STARTED);
            dyeBatch.setCreateAt(LocalDateTime.now());
            dyeBatch.setBatchNumber(i + 1);
            // Kiểm tra xem có phải là batches cuối không
            // Nếu là batches cuối thì sẽ tính toán chuẩn số trọng lượng còn lại
            BigDecimal currentConeBatchWeight = (i == dyeBatches - 1
                    && remainingConeWeight.compareTo(coneBatchWeight) < 0)
                    ? remainingConeWeight : coneBatchWeight;
            //
            dyeBatch.setPlannedOutput(currentConeBatchWeight.divide(dyeStage.getWorkOrderDetail().getPurchaseOrderDetail().getProduct().getThread().getConvert_rate(), 0, RoundingMode.CEILING).intValue());
            //
            dyeBatch.setCone_batch_weight(currentConeBatchWeight);
            dyeBatch.setLiters_min(dyeMachine.getLittersMin());
            dyeBatch.setLiters(currentConeBatchWeight.multiply(BigDecimal.valueOf(6)));
            dyeBatch.setDyeStage(dyeStage);
            dyeBatch.setPlannedStart(dyeStage.getPlannedStart().plusMinutes(i * 135));
            dyeBatch.setDeadline(dyeBatch.getPlannedStart().plusMinutes(105));
            assignTeamLeaderAndQAForDyeBatch(dyeBatch);
            //
            dyeBatchList.add(dyeBatch);
            //
            remainingConeWeight = remainingConeWeight.subtract(currentConeBatchWeight);
            System.err.println("Đã tạo DyeBatch với cone_batch_weight: " + currentConeBatchWeight
                    + " cho WorkOrderDetail ID: "
                    + (dyeStage.getWorkOrderDetail().getId() != null ? dyeStage.getWorkOrderDetail().getId() : "null"));
        }
        return dyeBatchList;
    }

    /**
     * Adds a WindingStage to the WorkOrderDetail.
     */
    private WindingStage addWindingStage(WorkOrderDetail workOrderDetail,
                                         List<Long> windingMachineIds,
                                         int windingMachineIndex) {
        // Khởi tạo stage côn
        WindingStage windingStage = initializeWindingStage(workOrderDetail);

        WindingMachine windingMachine = selectWindingMachine(workOrderDetail, windingMachineIds, windingMachineIndex);

        int windingBatches = workOrderDetail.getDyeStage().getDyebatches().size();

        LocalDateTime windingDeadline = calculateWindingDeadline(windingStage.getPlannedStart(), windingBatches, workOrderDetail);
        validateDeadline(windingDeadline, workOrderDetail.getWorkOrder().getDeadline(), "Winding Stage", workOrderDetail.getProductionOrderDetail().getId());

        windingStage.setDeadline(windingDeadline);
        windingStage.setWindingMachine(windingMachine);

        // Lưu WindingStage trước khi tạo và lưu WindingBatch
        windingStage = windingStageRepository.save(windingStage);

        // Khởi tạo các mẻ côn
        List<WindingBatch> windingBatchList = createWindingBatches(windingStage, windingBatches);
        windingBatchList = windingBatchRepository.saveAll(windingBatchList);
        windingStage.setWindingbatches(windingBatchList);

        // Gán team leaders và QA
        assignTeamLeadersAndQAForWindingStage(windingStage);

        // Lưu lại WindingStage sau khi cập nhật danh sách WindingBatch
        windingStage = windingStageRepository.save(windingStage);

        System.err.println("Đã thêm WindingStage cho WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        return windingStage;
    }

    /**
     * Initializes a WindingStage with basic properties.
     */
    private WindingStage initializeWindingStage(WorkOrderDetail workOrderDetail) {
        WindingStage windingStage = new WindingStage();
        windingStage.setCreateAt(LocalDateTime.now());
        windingStage.setWorkOrderDetail(workOrderDetail);
        windingStage.setDyeStage(workOrderDetail.getDyeStage());
        int dyeBatches = workOrderDetail.getDyeStage().getDyebatches().size();
        windingStage.setPlannedStart(dyeBatches > 1 ? workOrderDetail.getDyeStage().getPlannedStart().plusMinutes(270) : workOrderDetail.getDyeStage().getDeadline());
        windingStage.setWorkStatus(WorkEnum.NOT_STARTED);
        System.err.println("Đã khởi tạo WindingStage cho WorkOrderDetail ID: "
                + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        return windingStage;
    }

    /**
     * Selects a WindingMachine from the available list.
     */
    private WindingMachine selectWindingMachine(WorkOrderDetail workOrderDetail,
                                                List<Long> windingMachineIds,
                                                int index) {
        if (index >= windingMachineIds.size()) {
            System.err.println("Lỗi: Không đủ máy cuốn được chọn cho ProductionOrderDetail ID: "
                    + workOrderDetail.getProductionOrderDetail().getId());
            throw new IllegalArgumentException("Không đủ máy cuốn được chọn cho tất cả WorkOrderDetails");
        }
        Long windingMachineId = windingMachineIds.get(index);
        WindingMachine selectedMachine = windingMachineRepository.findById(windingMachineId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy máy cuốn với ID: " + windingMachineId));
        System.err.println("Đã chọn máy cuốn với ID: " + windingMachineId + " cho ProductionOrderDetail ID: "
                + workOrderDetail.getProductionOrderDetail().getId());
        return selectedMachine;
    }

    /**
     * Calculates the deadline for WindingStage.
     */
    private LocalDateTime calculateWindingDeadline(LocalDateTime plannedStart,
                                                   int windingBatches,
                                                   WorkOrderDetail workOrderDetail) {
        //
        long windingDurationMinutes = windingBatches * 60 + (windingBatches - 1) * 15;
        LocalDateTime deadline = plannedStart.plusMinutes(windingDurationMinutes);

        // Lấy actualDeliveryDate từ ProductionOrder
        LocalDate actualDeliveryDate = workOrderDetail.getWorkOrder().getProductionOrder()
                .getPurchaseOrder().getSolution().getActualDeliveryDate();
        LocalDateTime actualDeliveryDateTime = actualDeliveryDate.atTime(LocalTime.MAX);

        // Cộng thêm 1 ngày vào deadline
        LocalDateTime adjustedDeadline = deadline.plusDays(1);

        // Kiểm tra nếu deadline sau khi cộng vượt quá actualDeliveryDate thì không cộng
        if (adjustedDeadline.isAfter(actualDeliveryDateTime)) {
            System.err.println("Deadline của WindingStage không được cộng thêm 1 ngày vì vượt quá actualDeliveryDate: "
                    + actualDeliveryDate + " cho WorkOrderDetail ID: "
                    + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        } else {
            deadline = adjustedDeadline;
        }

        System.err.println("Đã tính toán thời hạn WindingStage: " + deadline + " với " + windingBatches + " lô");
        return deadline;
    }

    /**
     * Creates WindingBatches for the WindingStage.
     */
    private List<WindingBatch> createWindingBatches(WindingStage windingStage, int windingBatches) {
        List<WindingBatch> windingBatchList = new ArrayList<>();
        DyeStage dyeStage = windingStage.getWorkOrderDetail().getDyeStage();
        List<DyeBatch> dyeBatches = dyeStage.getDyebatches();

        for (int i = 0; i < windingBatches; i++) {
            WindingBatch windingBatch = new WindingBatch();
            DyeBatch dyeBatch = dyeBatches.get(i);
            windingBatch.setDyeBatch(dyeBatch);
            windingBatch.setWorkStatus(WorkEnum.NOT_STARTED);
            windingBatch.setTestStatus(TestEnum.NOT_STARTED);
            //
            windingBatch.setBatchNumber(i + 1);
            //
            windingBatch.setPlannedOutput(dyeBatch.getCone_batch_weight().divide(windingStage.getWorkOrderDetail().getPurchaseOrderDetail().getProduct().getThread().getConvert_rate(), 0, RoundingMode.CEILING).intValue());
            //
            windingBatch.setCreateAt(LocalDateTime.now());
            windingBatch.setWindingStage(windingStage);
            windingBatch.setPlannedStart(windingStage.getPlannedStart().plusMinutes(i * 75));
            windingBatch.setDeadline(windingBatch.getPlannedStart().plusMinutes(60));
            assignTeamLeaderAndQAForWindingBatch(windingBatch);
            windingBatchList.add(windingBatch);
            System.err.println("Đã tạo WindingBatch cho WorkOrderDetail ID: "
                    + (windingStage.getWorkOrderDetail().getId() != null
                    ? windingStage.getWorkOrderDetail().getId() : "null"));
        }
        return windingBatchList;
    }

    /**
     * Adds a PackagingStage to the WorkOrderDetail.
     */
    private PackagingStage addPackagingStage(WorkOrderDetail workOrderDetail) {
        // Khởi tạo stage đóng gói
        PackagingStage packagingStage = initializePackagingStage(workOrderDetail);
        int packagingBatches = workOrderDetail.getDyeStage().getDyebatches().size();

        BigDecimal totalPackagingDurationMinutes = calculatePackagingDuration(workOrderDetail, packagingBatches);

        LocalDateTime packagingDeadline = calculatePackagingDeadline(packagingStage.getPlannedStart(), totalPackagingDurationMinutes, workOrderDetail);

        validateDeadline(packagingDeadline, workOrderDetail.getWorkOrder().getDeadline(), "Packaging Stage", workOrderDetail.getProductionOrderDetail().getId());

        packagingStage.setDeadline(packagingDeadline);

        // Lưu PackagingStage trước khi tạo và lưu PackagingBatch
        packagingStage = packagingStageRepository.save(packagingStage);

        // Khởi tạo batch đóng gói
        List<PackagingBatch> packagingBatchesList = createPackagingBatches(packagingStage, packagingBatches);
        packagingBatchesList = packagingBatchRepository.saveAll(packagingBatchesList);
        packagingStage.setPackagingBatches(packagingBatchesList);

        // Gán team leaders và QA
        assignTeamLeadersAndQAForPackagingStage(packagingStage);

        // Lưu lại PackagingStage sau khi cập nhật danh sách PackagingBatch
        packagingStage = packagingStageRepository.save(packagingStage);

        System.err.println("Đã thêm PackagingStage cho WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        return packagingStage;
    }

    /**
     * Initializes a PackagingStage with basic properties.
     */
    private PackagingStage initializePackagingStage(WorkOrderDetail workOrderDetail) {
        PackagingStage packagingStage = new PackagingStage();
        packagingStage.setCreateAt(LocalDateTime.now());
        packagingStage.setWorkOrderDetail(workOrderDetail);
        packagingStage.setWindingStage(workOrderDetail.getWindingStage());
        int dyeBatches = workOrderDetail.getDyeStage().getDyebatches().size();
        packagingStage.setPlannedStart(dyeBatches > 1 ? workOrderDetail.getWindingStage().getPlannedStart().plusMinutes(150) : workOrderDetail.getWindingStage().getDeadline());
        packagingStage.setWorkStatus(WorkEnum.NOT_STARTED);
        System.err.println("Đã khởi tạo PackagingStage cho WorkOrderDetail ID: "
                + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        return packagingStage;
    }

    /**
     * Calculates the total duration for PackagingStage.
     */
    private BigDecimal calculatePackagingDuration(WorkOrderDetail workOrderDetail, int packagingBatches) {
        // 1 sản phẩm mất 30s để đóng gói
        BigDecimal packagingTimePerProduct = BigDecimal.valueOf(0.5);
        BigDecimal totalPackagingDurationMinutes = BigDecimal.ZERO;

        // Tính toán lại số sản phẩm có trong mẻ
        BigDecimal convertRate = workOrderDetail.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate();
        for (DyeBatch dyeBatch : workOrderDetail.getDyeStage().getDyebatches()) {
            BigDecimal productsInBatch = dyeBatch.getCone_batch_weight().divide(convertRate);
            totalPackagingDurationMinutes = totalPackagingDurationMinutes.add(productsInBatch.multiply(packagingTimePerProduct));
        }

        System.err.println("Đã tính toán thời gian PackagingStage: "
                + totalPackagingDurationMinutes + " phút cho WorkOrderDetail ID: "
                + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));

        return totalPackagingDurationMinutes;
    }

    private LocalDateTime calculatePackagingDeadline(LocalDateTime plannedStart,
                                                     BigDecimal totalPackagingDurationMinutes,
                                                     WorkOrderDetail workOrderDetail) {
        LocalDateTime deadline = plannedStart.plusMinutes(totalPackagingDurationMinutes.longValue());

        // Lấy actualDeliveryDate từ ProductionOrder
        LocalDate actualDeliveryDate = workOrderDetail.getWorkOrder().getProductionOrder()
                .getPurchaseOrder().getSolution().getActualDeliveryDate();
        LocalDateTime actualDeliveryDateTime = actualDeliveryDate.atTime(LocalTime.MAX);

        // Cộng thêm 1 ngày vào deadline
        LocalDateTime adjustedDeadline = deadline.plusDays(1);

        // Kiểm tra nếu deadline sau khi cộng vượt quá actualDeliveryDate thì không cộng
        if (adjustedDeadline.isAfter(actualDeliveryDateTime)) {
            System.err.println("Deadline của PackagingStage không được cộng thêm 1 ngày vì vượt quá actualDeliveryDate: "
                    + actualDeliveryDate + " cho WorkOrderDetail ID: "
                    + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        } else {
            deadline = adjustedDeadline;
        }

        return deadline;
    }

    /**
     * Creates PackagingBatches for the PackagingStage.
     */
    private List<PackagingBatch> createPackagingBatches(PackagingStage packagingStage, int packagingBatches) {
        //
        List<PackagingBatch> packagingBatchList = new ArrayList<>();
        WindingStage windingStage = packagingStage.getWorkOrderDetail().getWindingStage();
        List<WindingBatch> windingBatches = windingStage.getWindingbatches();

        //
        BigDecimal convertRate = packagingStage.getWorkOrderDetail().getPurchaseOrderDetail().getProduct().getThread().getConvert_rate();
        BigDecimal packagingTimePerProduct = BigDecimal.valueOf(0.5);

        //
        for (int i = 0; i < packagingBatches; i++) {
            PackagingBatch packagingBatch = new PackagingBatch();
            WindingBatch windingBatch = windingBatches.get(i);
            packagingBatch.setWindingBatch(windingBatch);
            packagingBatch.setCreateAt(LocalDateTime.now());
            packagingBatch.setPackagingStage(packagingStage);
            packagingBatch.setWorkStatus(WorkEnum.NOT_STARTED);
            packagingBatch.setTestStatus(TestEnum.NOT_STARTED);
            //
            packagingBatch.setBatchNumber(i + 1);
            //
            BigDecimal productsInBatch = packagingStage.getWorkOrderDetail().getDyeStage().getDyebatches().get(i).
                    getCone_batch_weight().divide(convertRate);
            //
            packagingBatch.setPlannedOutput(productsInBatch.intValue());
            //
            long batchDurationMinutes = productsInBatch.multiply(packagingTimePerProduct).longValue();
            //
            packagingBatch.setPlannedStart(i == 0 ? packagingStage.getPlannedStart() : packagingBatchList.get(i - 1).getDeadline());
            packagingBatch.setDeadline(packagingBatch.getPlannedStart().plusMinutes(batchDurationMinutes));
            assignTeamLeaderAndQAForPackagingBatch(packagingBatch);

            //
            packagingBatchList.add(packagingBatch);
            System.err.println("Đã tạo PackagingBatch cho WorkOrderDetail ID: "
                    + (packagingStage.getWorkOrderDetail().getId() != null ?
                    packagingStage.getWorkOrderDetail().getId() : "null"));
        }
        return packagingBatchList;
    }

    @Override
    public WorkOrder findWorkOrderByProductionOrder(ProductionOrder productionOrder) {
        return workOrderRepository.findByProductionOrder(productionOrder);
    }

    private String getRoleForStageType(StageType stageType) {
        switch (stageType) {
            case DYE:
                return "LEAD_DYE";
            case WINDING:
                return "LEAD_WINDING";
            case PACKAGING:
                return "LEAD_PACKAGING";
            default:
                throw new IllegalArgumentException("Không xác định được loại giai đoạn: " + stageType);
        }
    }

    private User findTeamLeaderForShift(Shift shift, StageType stageType) {
        String requiredRole = getRoleForStageType(stageType);
        return shift.getUserShifts().stream()
                .map(UserShift::getUser)
                .filter(user -> user.getRole().getName().equals(requiredRole))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trưởng nhóm cho ca làm việc " + shift.getId()));
    }

    private User findQAForShift(Shift shift, StageType stageType) {
        String requiredRole;
        switch (stageType) {
            case DYE:
                requiredRole = "QA_DYE";
                break;
            case WINDING:
                requiredRole = "QA_WINDING";
                break;
            case PACKAGING:
                requiredRole = "QA_PACKAGING";
                break;
            default:
                throw new IllegalArgumentException("Không xác định được loại giai đoạn: " + stageType);
        }

        return shift.getUserShifts().stream()
                .map(UserShift::getUser)
                .filter(user -> user.getRole().getName().equals(requiredRole))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy QA cho giai đoạn " + stageType + " trong ca làm việc " + shift.getShiftName()));

    }

    private Shift findShiftForTime(LocalTime time, LocalDate date) {
        List<Shift> shifts = shiftRepository.findAll();
        return shifts.stream().filter(shift -> {
            LocalTime shiftStart = shift.getShiftStart();
            LocalTime shiftEnd = shift.getShiftEnd();
            if (shiftEnd.isBefore(shiftStart)) { // Qua ngày, ví dụ: 0h-8h
                return (time.isAfter(shiftStart) && time.isBefore(LocalTime.MAX)) || (time.isBefore(shiftEnd) && time.isAfter(LocalTime.MIN));
            }
            return time.isAfter(shiftStart) && time.isBefore(shiftEnd);
        }).findFirst().orElseThrow(() -> new IllegalStateException("Không tìm thấy ca làm việc cho thời gian: " + time + " vào ngày " + date));

    }

    private void assignTeamLeadersAndQAForDyeStage(DyeStage dyeStage) {
        List<User> teamLeaders = new ArrayList<>();
        List<User> qaList = new ArrayList<>();
        LocalDateTime start = dyeStage.getPlannedStart();
        LocalDateTime end = dyeStage.getDeadline();
        LocalDateTime current = start;

        while (current.isBefore(end)) {
            LocalDate shiftDate = current.toLocalDate();
            Shift shift = findShiftForTime(current.toLocalTime(), shiftDate);
            User teamLeader = findTeamLeaderForShift(shift, StageType.DYE);
            // Truyền StageType.DYE vào findQAForShift
            User qa = findQAForShift(shift, StageType.DYE);
            if (teamLeader != null && !teamLeaders.contains(teamLeader)) {
                teamLeaders.add(teamLeader);
            }
            if (qa != null && !qaList.contains(qa)) {
                qaList.add(qa);
            }
            current = current.plusHours(8);
        }
        dyeStage.setTeamLeaders(teamLeaders);
        dyeStage.setQa(qaList);
    }

    private void assignTeamLeaderAndQAForDyeBatch(DyeBatch batch) {
        LocalDateTime start = batch.getPlannedStart();
        LocalDateTime end = batch.getDeadline();
        LocalDate shiftDate = start.toLocalDate();
        Shift shift = findShiftForTime(start.toLocalTime(), shiftDate);

        User teamLeaderStart = findTeamLeaderForShift(shift, StageType.DYE);
        batch.setLeaderStart(teamLeaderStart);

        Shift endShift = findShiftForTime(end.toLocalTime(), end.toLocalDate());
        if (!shift.equals(endShift)) {
            User teamLeaderEnd = findTeamLeaderForShift(endShift, StageType.DYE);
            batch.setLeaderEnd(teamLeaderEnd);
            // Truyền StageType.DYE vào findQAForShift
            batch.setQa(findQAForShift(endShift, StageType.DYE));
        } else {
            // Truyền StageType.DYE vào findQAForShift
            batch.setQa(findQAForShift(shift, StageType.DYE));
        }
    }

    private void assignTeamLeadersAndQAForWindingStage(WindingStage windingStage) {
        List<User> teamLeaders = new ArrayList<>();
        List<User> qaList = new ArrayList<>();
        LocalDateTime start = windingStage.getPlannedStart();
        LocalDateTime end = windingStage.getDeadline();
        LocalDateTime current = start;

        while (current.isBefore(end)) {
            LocalDate shiftDate = current.toLocalDate();
            Shift shift = findShiftForTime(current.toLocalTime(), shiftDate);
            User teamLeader = findTeamLeaderForShift(shift, StageType.WINDING);
            // Truyền StageType.WINDING vào findQAForShift
            User qa = findQAForShift(shift, StageType.WINDING);
            if (teamLeader != null && !teamLeaders.contains(teamLeader)) {
                teamLeaders.add(teamLeader);
            }
            if (qa != null && !qaList.contains(qa)) {
                qaList.add(qa);
            }
            current = current.plusHours(8);
        }
        windingStage.setTeamLeaders(teamLeaders);
        windingStage.setQa(qaList);
    }

    private void assignTeamLeaderAndQAForWindingBatch(WindingBatch batch) {
        LocalDateTime start = batch.getPlannedStart();
        LocalDateTime end = batch.getDeadline();
        LocalDate shiftDate = start.toLocalDate();
        Shift shift = findShiftForTime(start.toLocalTime(), shiftDate);

        User teamLeaderStart = findTeamLeaderForShift(shift, StageType.WINDING);
        batch.setLeaderStart(teamLeaderStart);

        Shift endShift = findShiftForTime(end.toLocalTime(), shiftDate);
        if (!shift.equals(endShift)) {
            User teamLeaderEnd = findTeamLeaderForShift(endShift, StageType.WINDING);
            batch.setLeaderEnd(teamLeaderEnd);
            // Truyền StageType.WINDING vào findQAForShift
            batch.setQa(findQAForShift(endShift, StageType.WINDING));
        } else {
            // Truyền StageType.WINDING vào findQAForShift
            batch.setQa(findQAForShift(shift, StageType.WINDING));
        }
    }

    private void assignTeamLeadersAndQAForPackagingStage(PackagingStage packagingStage) {
        List<User> teamLeaders = new ArrayList<>();
        List<User> qaList = new ArrayList<>();
        LocalDateTime start = packagingStage.getPlannedStart();
        LocalDateTime end = packagingStage.getDeadline();
        LocalDateTime current = start;

        while (current.isBefore(end)) {
            LocalDate shiftDate = current.toLocalDate();
            Shift shift = findShiftForTime(current.toLocalTime(), shiftDate);
            User teamLeader = findTeamLeaderForShift(shift, StageType.PACKAGING);
            // Truyền StageType.PACKAGING vào findQAForShift
            User qa = findQAForShift(shift, StageType.PACKAGING);
            if (teamLeader != null && !teamLeaders.contains(teamLeader)) {
                teamLeaders.add(teamLeader);
            }
            if (qa != null && !qaList.contains(qa)) {
                qaList.add(qa);
            }
            current = current.plusHours(8);
        }
        packagingStage.setTeamLeaders(teamLeaders);
        packagingStage.setQa(qaList);
    }

    private void assignTeamLeaderAndQAForPackagingBatch(PackagingBatch batch) {
        LocalDateTime start = batch.getPlannedStart();
        LocalDateTime end = batch.getDeadline();
        LocalDate shiftDate = start.toLocalDate();
        Shift shift = findShiftForTime(start.toLocalTime(), shiftDate);

        User teamLeaderStart = findTeamLeaderForShift(shift, StageType.PACKAGING);
        batch.setLeaderStart(teamLeaderStart);

        Shift endShift = findShiftForTime(end.toLocalTime(), end.toLocalDate());
        if (!shift.equals(endShift)) {
            User teamLeaderEnd = findTeamLeaderForShift(endShift, StageType.PACKAGING);
            batch.setLeaderEnd(teamLeaderEnd);
            // Truyền StageType.PACKAGING vào findQAForShift
            batch.setQa(findQAForShift(endShift, StageType.PACKAGING));
        } else {
            // Truyền StageType.PACKAGING vào findQAForShift
            batch.setQa(findQAForShift(shift, StageType.PACKAGING));
        }
    }

    @Override
    @Transactional
    public void deleteWorkOrderDetails(Long workOrderId) {
        try {
            // 1. Tìm WorkOrder
            WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Work Order với ID: " + workOrderId));

            // 2. Kiểm tra trạng thái của WorkOrder
            boolean canDelete = (workOrder.getStatus() == BaseEnum.DRAFT && workOrder.getSendStatus() == SendEnum.NOT_SENT) ||
                    (workOrder.getStatus() == BaseEnum.NOT_APPROVED && workOrder.getSendStatus() == SendEnum.SENT);
            if (!canDelete) {
                System.err.println("Lỗi: Không thể xóa WorkOrderDetails của WorkOrder ID: " + workOrderId +
                        " vì trạng thái không hợp lệ. Hiện tại: status=" + workOrder.getStatus() +
                        ", sendStatus=" + workOrder.getSendStatus());
                throw new IllegalStateException("Chỉ có thể xóa WorkOrderDetails khi ở trạng thái DRAFT và NOT_SENT, hoặc NOT_APPROVED và SENT");
            }

            // 3. Lấy danh sách WorkOrderDetail
            List<WorkOrderDetail> workOrderDetails = new ArrayList<>(workOrder.getWorkOrderDetails());
            if (workOrderDetails.isEmpty()) {
                System.err.println("Không có WorkOrderDetails để xóa cho WorkOrder ID: " + workOrderId);
                return;
            }
            System.err.println("SIZE WORK ORDER DETAIL TRƯỚC KHI XOÁ: " + workOrderDetails.size());

            // 4. Lấy danh sách DyeStage, WindingStage, PackagingStage liên quan
            List<DyeStage> dyeStages = workOrderDetails.stream()
                    .map(WorkOrderDetail::getDyeStage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<WindingStage> windingStages = workOrderDetails.stream()
                    .map(WorkOrderDetail::getWindingStage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<PackagingStage> packagingStages = workOrderDetails.stream()
                    .map(WorkOrderDetail::getPackagingStage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 5. Lấy danh sách ID của DyeStage, WindingStage, PackagingStage
            List<Long> dyeStageIds = dyeStages.stream()
                    .map(DyeStage::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<Long> windingStageIds = windingStages.stream()
                    .map(WindingStage::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<Long> packagingStageIds = packagingStages.stream()
                    .map(PackagingStage::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 6. Lấy danh sách DyeBatch, WindingBatch, PackagingBatch trực tiếp từ cơ sở dữ liệu
            List<Long> dyeBatchIds = dyeStageIds.isEmpty() ? new ArrayList<>() :
                    dyeBatchRepository.findAllByDyeStageIdIn(dyeStageIds).stream()
                            .map(DyeBatch::getId)
                            .collect(Collectors.toList());
            dyeBatchRepository.flush();

            List<Long> windingBatchIds = windingStageIds.isEmpty() ? new ArrayList<>() :
                    windingBatchRepository.findAllByWindingStageIdIn(windingStageIds).stream()
                            .map(WindingBatch::getId)
                            .collect(Collectors.toList());
            windingBatchRepository.flush();

            List<Long> packagingBatchIds = packagingStageIds.isEmpty() ? new ArrayList<>() :
                    packagingBatchRepository.findAllByPackagingStageIdIn(packagingStageIds).stream()
                            .map(PackagingBatch::getId)
                            .collect(Collectors.toList());
            packagingBatchRepository.flush();

            // 7. Xóa PhotoStage
            if (!packagingBatchIds.isEmpty()) {
                photoStageRepository.deleteByPackagingBatchIds(packagingBatchIds);
                photoStageRepository.flush();
            }

            if (!windingBatchIds.isEmpty()) {
                photoStageRepository.deleteByWindingBatchIds(windingBatchIds);
                photoStageRepository.flush();
            }

            if (!dyeBatchIds.isEmpty()) {
                photoStageRepository.deleteByDyeBatchIds(dyeBatchIds);
                photoStageRepository.flush();
            }

            // 8. Làm sạch session Hibernate sau khi xóa PhotoStage
            entityManager.clear();

            // 9. Xóa RiskSolution và RiskAssessment
            if (!packagingBatchIds.isEmpty()) {
                riskSolutionRepository.deleteByPackagingBatchIds(packagingBatchIds);
                riskSolutionRepository.flush();

                packagingRiskAssessmentRepository.deleteAllByPackagingBatchIds(packagingBatchIds);
                packagingRiskAssessmentRepository.flush();
            }

            if (!windingBatchIds.isEmpty()) {
                riskSolutionRepository.deleteByWindingBatchIds(windingBatchIds);
                riskSolutionRepository.flush();

                windingRiskAssessmentRepository.deleteAllByWindingBatchIds(windingBatchIds);
                windingRiskAssessmentRepository.flush();
            }

            if (!dyeBatchIds.isEmpty()) {
                riskSolutionRepository.deleteByDyeBatchIds(dyeBatchIds);
                riskSolutionRepository.flush();

                dyeRiskAssessmentRepository.deleteAllByDyeBatchIds(dyeBatchIds);
                dyeRiskAssessmentRepository.flush();
            }

            // 10. Làm sạch session Hibernate sau khi xóa RiskSolution và RiskAssessment
            entityManager.clear();

            // 11. Xóa các Batch
            if (!packagingBatchIds.isEmpty()) {
                packagingBatchRepository.deleteAllById(packagingBatchIds);
                packagingBatchRepository.flush();
            }

            if (!windingBatchIds.isEmpty()) {
                windingBatchRepository.deleteAllById(windingBatchIds);
                windingBatchRepository.flush();
            }

            if (!dyeBatchIds.isEmpty()) {
                dyeBatchRepository.deleteAllById(dyeBatchIds);
                dyeBatchRepository.flush();
            }

            // 12. Làm sạch session Hibernate sau khi xóa Batch
            entityManager.clear();

            // 13. Xóa các bản ghi trong bảng trung gian (teamLeaders, qa) trước khi xóa Stage
            if (!packagingStageIds.isEmpty()) {
                packagingStageRepository.deleteTeamLeadersByPackagingStageIds(packagingStageIds);
                packagingStageRepository.deleteQaByPackagingStageIds(packagingStageIds);
                packagingStageRepository.flush();
            }

            if (!windingStageIds.isEmpty()) {
                windingStageRepository.deleteTeamLeadersByWindingStageIds(windingStageIds);
                windingStageRepository.deleteQaByWindingStageIds(windingStageIds);
                windingStageRepository.flush();
            }

            if (!dyeStageIds.isEmpty()) {
                dyeStageRepository.deleteTeamLeadersByDyeStageIds(dyeStageIds);
                dyeStageRepository.deleteQaByDyeStageIds(dyeStageIds);
                dyeStageRepository.flush();
            }

            // 14. Xóa các Stage
            if (!workOrderDetails.isEmpty()) {
                packagingStageRepository.deleteAllByWorkOrderDetailIn(workOrderDetails);
                packagingStageRepository.flush();

                windingStageRepository.deleteAllByWorkOrderDetailIn(workOrderDetails);
                windingStageRepository.flush();

                dyeStageRepository.deleteAllByWorkOrderDetailIn(workOrderDetails);
                dyeStageRepository.flush();
            }

            // 15. Làm sạch session Hibernate sau khi xóa Stage
            entityManager.clear();

            // 16. Xóa WorkOrderDetail
            workOrderDetailRepository.deleteAllByWorkOrder(workOrder);
            workOrderDetailRepository.flush();

            // 17. Làm mới WorkOrder từ cơ sở dữ liệu
            workOrder = workOrderRepository.findById(workOrderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Work Order với ID: " + workOrderId));

            // 18. Làm sạch session Hibernate trước khi cập nhật WorkOrder
            entityManager.clear();

            // 19. Cập nhật WorkOrder
            workOrder.setStatus(BaseEnum.WAIT_FOR_UPDATE);
            workOrder.setSendStatus(SendEnum.NOT_SENT);
            workOrderRepository.saveAndFlush(workOrder);

            // 20. Kiểm tra lại số lượng WorkOrderDetail
            long detailCount = workOrderDetailRepository.countByWorkOrder_Id(workOrderId);
            System.err.println("Số lượng WorkOrderDetail còn lại trong DB: " + detailCount);
            if (detailCount > 0) {
                throw new IllegalStateException("Xóa WorkOrderDetails không hoàn tất. Vẫn còn " + detailCount + " bản ghi tồn tại.");
            }

            System.err.println("Đã xóa toàn bộ WorkOrderDetails của WorkOrder ID: " + workOrderId + " thành công");

        } catch (Exception e) {
            System.err.println("Lỗi khi xóa WorkOrderDetails của WorkOrder ID: " + workOrderId + " - " + e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public WorkOrder updateWorkOrder(Long workOrderId,
                                     List<Long> selectedDyeMachineIds,
                                     List<Long> selectedWindingMachineIds,
                                     List<BigDecimal> additionalWeights) {
        try {
            // 1. Tìm WorkOrder
            WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                    .orElseThrow(() -> new IllegalArgumentException("WorkOrder không tồn tại với ID: " + workOrderId));

            // 2. Kiểm tra trạng thái của WorkOrder
            if (workOrder.getStatus() != BaseEnum.WAIT_FOR_UPDATE) {
                System.err.println("Lỗi: WorkOrder ID: " + workOrderId + " không ở trạng thái WAIT_FOR_UPDATE. Hiện tại: " + workOrder.getStatus());
                throw new IllegalStateException("WorkOrder không ở trạng thái có thể cập nhật.");
            }

            // 3. Lấy ProductionOrder
            ProductionOrder productionOrder = workOrder.getProductionOrder();
            if (productionOrder == null) {
                System.err.println("Lỗi: ProductionOrder không tồn tại cho WorkOrder ID: " + workOrderId);
                throw new IllegalStateException("ProductionOrder không tồn tại.");
            }

            // 4. Validate input với eager loading cho Thread
            validateInput(productionOrder, workOrder.getCreatedBy(), selectedDyeMachineIds, selectedWindingMachineIds, additionalWeights);

            // 6. Kiểm tra tính khả dụng của các máy trước khi tạo WorkOrderDetails
            LocalDateTime plannedStartAt = LocalDateTime.now().plusHours(2);
            LocalDateTime plannedEndAt = workOrder.getDeadline().atTime(LocalTime.MAX);

            // Khóa các máy được chọn để tránh đồng thời
            List<DyeMachine> lockedDyeMachines = new ArrayList<>();
            List<WindingMachine> lockedWindingMachines = new ArrayList<>();

            // Khóa máy nhuộm
            for (Long dyeMachineId : selectedDyeMachineIds) {
                DyeMachine dyeMachine = dyeMachineRepository.findByIdWithLock(dyeMachineId);
                if (dyeMachine == null) {
                    throw new IllegalArgumentException("Không tìm thấy Dye Machine ID " + dyeMachineId);
                }
                lockedDyeMachines.add(dyeMachine);
            }

            // Khóa máy cuốn
            for (Long windingMachineId : selectedWindingMachineIds) {
                WindingMachine windingMachine = windingMachineRepository.findByIdWithLock(windingMachineId);
                if (windingMachine == null) {
                    throw new IllegalArgumentException("Không tìm thấy Winding Machine ID " + windingMachineId);
                }
                lockedWindingMachines.add(windingMachine);
            }

            // Tạo một WorkOrderDetail tạm thời để kiểm tra tính khả dụng
            for (int i = 0; i < productionOrder.getProductionOrderDetails().size(); i++) {
                WorkOrderDetail tempWorkOrderDetail = new WorkOrderDetail();
                tempWorkOrderDetail.setWorkOrder(workOrder);
                tempWorkOrderDetail.setProductionOrderDetail(productionOrder.getProductionOrderDetails().get(i));
                tempWorkOrderDetail.setPlannedStartAt(plannedStartAt);
                tempWorkOrderDetail.setPlannedEndAt(plannedEndAt);

                // Kiểm tra máy nhuộm
                if (i < selectedDyeMachineIds.size()) {
                    Long dyeMachineId = selectedDyeMachineIds.get(i);
                    List<DyeMachine> availableDyeMachines = machineService.findAvailableDyeMachines(tempWorkOrderDetail, plannedStartAt, plannedEndAt);
                    if (availableDyeMachines.stream().noneMatch(m -> m.getId().equals(dyeMachineId))) {
                        System.err.println("Lỗi: Máy nhuộm được chọn với ID " + dyeMachineId
                                + " không khả dụng cho ProductionOrderDetail ID: "
                                + tempWorkOrderDetail.getProductionOrderDetail().getId());
                        throw new IllegalArgumentException("Máy nhuộm được chọn với ID "
                                + dyeMachineId + " không khả dụng");
                    }
                }

                // Kiểm tra máy cuốn
                if (i < selectedWindingMachineIds.size()) {
                    Long windingMachineId = selectedWindingMachineIds.get(i);
                    List<WindingMachine> availableWindingMachines = machineService.findAvailableWindingMachines(tempWorkOrderDetail, plannedStartAt, plannedEndAt);
                    if (availableWindingMachines.stream().noneMatch(m -> m.getId().equals(windingMachineId))) {
                        System.err.println("Lỗi: Máy cuốn được chọn với ID " + windingMachineId
                                + " không khả dụng cho ProductionOrderDetail ID: "
                                + tempWorkOrderDetail.getProductionOrderDetail().getId());
                        throw new IllegalArgumentException("Máy cuốn được chọn với ID "
                                + windingMachineId + " không khả dụng");
                    }
                }
            }

            // 7. Xóa các WorkOrderDetails cũ
            List<WorkOrderDetail> existingDetails = new ArrayList<>(workOrder.getWorkOrderDetails());
            if (!existingDetails.isEmpty()) {
                workOrderDetailRepository.deleteAll(existingDetails);
                workOrderDetailRepository.flush();
                workOrder.setWorkOrderDetails(new ArrayList<>()); // Cập nhật danh sách rỗng
            }

            // 8. Tạo lại WorkOrderDetails mới
            List<WorkOrderDetail> workOrderDetails = addWorkOrderDetails(workOrder, productionOrder, selectedDyeMachineIds, selectedWindingMachineIds, additionalWeights);

            // 9. Đồng bộ hóa quan hệ hai chiều
            for (WorkOrderDetail detail : workOrderDetails) {
                detail.setWorkOrder(workOrder); // Đồng bộ quan hệ hai chiều
                workOrder.getWorkOrderDetails().add(detail);
            }

            // 10. Lưu và flush để đảm bảo dữ liệu được ghi vào cơ sở dữ liệu
            workOrderRepository.saveAndFlush(workOrder);

            // 11. Cập nhật trạng thái WorkOrder
            workOrder.setStatus(BaseEnum.DRAFT);
            workOrder.setSendStatus(SendEnum.NOT_SENT);
            workOrder.setUpdateAt(LocalDateTime.now());

            // 12. Lưu WorkOrder và flush lại
            WorkOrder updatedWorkOrder = workOrderRepository.saveAndFlush(workOrder);

            System.err.println("Đã cập nhật WorkOrder ID: " + workOrderId + " thành công và đã flush dữ liệu");

            return updatedWorkOrder;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật WorkOrder ID: " + workOrderId + " - " + e.getMessage());
            throw e; // Đảm bảo ném exception để rollback nếu cần
        }
    }
}