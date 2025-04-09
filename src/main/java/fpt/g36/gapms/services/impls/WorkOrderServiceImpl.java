package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.*;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.repositories.DyeMachineRepository;
import fpt.g36.gapms.repositories.WindingMachineRepository;
import fpt.g36.gapms.repositories.ProductionOrderRepository;
import fpt.g36.gapms.repositories.ShiftRepository;
import fpt.g36.gapms.repositories.WorkOrderRepository;
import fpt.g36.gapms.services.MachineService;
import fpt.g36.gapms.services.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Page<WorkOrder> getAllWorkOrderTeamLeader(Pageable pageable, String workOrderId) {
        Long id = null;
        if (workOrderId != null && !workOrderId.trim().isEmpty()) {
            try {
                // Loại bỏ tiền tố "WO-" nếu có và chuyển thành Long
                String numericPart = workOrderId.replace("WO-", "").trim();
                id = Long.parseLong(numericPart);
            } catch (NumberFormatException e) {
                // Nếu không parse được, trả về null hoặc xử lý theo cách khác
                id = null;
            }
        }
        return workOrderRepository.getAllWorkOrderTeamLeader(id, pageable);
    }

    @Override
    public Page<WorkOrder> getAllWorkOrders(Pageable pageable) {
        return workOrderRepository.findAllByOrderByCreateAt(pageable);
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
    @Transactional
    public WorkOrder createWorkOrder(ProductionOrder productionOrder, User createBy, List<Long> selectedDyeMachineIds, List<Long> selectedWindingMachineIds) {
        try {
            validateInput(productionOrder, createBy, selectedDyeMachineIds, selectedWindingMachineIds);

            WorkOrder existingWorkOrder = findWorkOrderByProductionOrder(productionOrder);
            if (existingWorkOrder != null) {
                System.err.println("Error: Work Order already exists for ProductionOrder ID: " + productionOrder.getId());
                throw new IllegalArgumentException("Work Order already exists!");
            }

            // Kiểm tra tính khả dụng của các máy trước khi tạo WorkOrderDetails
            WorkOrder newWorkOrder = initializeWorkOrder(productionOrder, createBy);
            LocalDateTime plannedStartAt = LocalDateTime.now().plusHours(2);
            LocalDateTime plannedEndAt = newWorkOrder.getDeadline().atTime(LocalTime.MAX);

            // Khóa các máy được chọn để tránh đồng thời
            List<DyeMachine> lockedDyeMachines = new ArrayList<>();
            List<WindingMachine> lockedWindingMachines = new ArrayList<>();

            // Khóa máy nhuộm
            for (Long dyeMachineId : selectedDyeMachineIds) {
                DyeMachine dyeMachine = dyeMachineRepository.findByIdWithLock(dyeMachineId);
                if (dyeMachine == null) {
                    throw new IllegalArgumentException("Dye Machine ID " + dyeMachineId + " not found");
                }
                lockedDyeMachines.add(dyeMachine);
            }

            // Khóa máy cuốn
            for (Long windingMachineId : selectedWindingMachineIds) {
                WindingMachine windingMachine = windingMachineRepository.findByIdWithLock(windingMachineId);
                if (windingMachine == null) {
                    throw new IllegalArgumentException("Winding Machine ID " + windingMachineId + " not found");
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
                    List<DyeMachine> availableDyeMachines = machineService.findAvailableDyeMachines(
                            tempWorkOrderDetail, plannedStartAt, plannedEndAt);
                    if (availableDyeMachines.stream().noneMatch(m -> m.getId().equals(dyeMachineId))) {
                        System.err.println("Error: Selected Dye Machine ID " + dyeMachineId + " is not available for ProductionOrderDetail ID: " +
                                tempWorkOrderDetail.getProductionOrderDetail().getId());
                        throw new IllegalArgumentException("Selected Dye Machine ID " + dyeMachineId + " is not available");
                    }
                }

                // Kiểm tra máy cuốn
                if (i < selectedWindingMachineIds.size()) {
                    Long windingMachineId = selectedWindingMachineIds.get(i);
                    List<WindingMachine> availableWindingMachines = machineService.findAvailableWindingMachines(
                            tempWorkOrderDetail, plannedStartAt, plannedEndAt);
                    if (availableWindingMachines.stream().noneMatch(m -> m.getId().equals(windingMachineId))) {
                        System.err.println("Error: Selected Winding Machine ID " + windingMachineId + " is not available for ProductionOrderDetail ID: " +
                                tempWorkOrderDetail.getProductionOrderDetail().getId());
                        throw new IllegalArgumentException("Selected Winding Machine ID " + windingMachineId + " is not available");
                    }
                }
            }

            // Nếu tất cả máy đều khả dụng, tiến hành thêm WorkOrderDetails
            addWorkOrderDetails(newWorkOrder, productionOrder, selectedDyeMachineIds, selectedWindingMachineIds);

            return workOrderRepository.save(newWorkOrder);
        } catch (Exception e) {
            System.err.println("Error creating WorkOrder for ProductionOrder ID: " +
                    (productionOrder != null ? productionOrder.getId() : "null") + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Validates the input parameters for creating a WorkOrder.
     */
    private void validateInput(ProductionOrder productionOrder, User createBy, List<Long> dyeMachineIds, List<Long> windingMachineIds) {
        if (productionOrder == null || createBy == null) {
            System.err.println("Error: ProductionOrder or createBy is null");
            throw new IllegalArgumentException("ProductionOrder is null or createBy is null");
        }
        if (productionOrder.getPurchaseOrder() == null || productionOrder.getPurchaseOrder().getSolution() == null
                || productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate() == null) {
            System.err.println("Error: ActualDeliveryDate is null in ProductionOrder ID: " +
                    (productionOrder != null ? productionOrder.getId() : "null"));
            throw new IllegalArgumentException("ActualDeliveryDate is null");
        }
        if (dyeMachineIds == null || windingMachineIds == null) {
            System.err.println("Error: DyeMachineIds or WindingMachineIds list is null");
            throw new IllegalArgumentException("Machine ID lists cannot be null");
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
        workOrder.setDeadline(productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate().minusDays(1));
        workOrder.setStatus(BaseEnum.DRAFT);
        workOrder.setSendStatus(SendEnum.NOT_SENT);
        workOrder.setIsProduction(WorkEnum.NOT_STARTED);
        workOrder.setWorkOrderDetails(new ArrayList<>());
        System.err.println("Initialized WorkOrder for ProductionOrder ID: " + productionOrder.getId());
        return workOrder;
    }

    /**
     * Adds WorkOrderDetails to the WorkOrder based on ProductionOrderDetails.
     */
    private void addWorkOrderDetails(WorkOrder workOrder, ProductionOrder productionOrder, List<Long> dyeMachineIds, List<Long> windingMachineIds) {
        int dyeMachineIndex = 0;
        int windingMachineIndex = 0;

        for (ProductionOrderDetail detail : productionOrder.getProductionOrderDetails()) {
            try {
                WorkOrderDetail workOrderDetail = initializeWorkOrderDetail(workOrder, detail);
                addDyeStage(workOrderDetail, dyeMachineIds, dyeMachineIndex++);
                addWindingStage(workOrderDetail, windingMachineIds, windingMachineIndex++);
                addPackagingStage(workOrderDetail);
                workOrder.getWorkOrderDetails().add(workOrderDetail);
                System.err.println("Added WorkOrderDetail: " + workOrderDetail.getId() +
                        " for ProductionOrderDetail ID: " + detail.getId() +
                        " and PurchaseOrderDetail ID: " + workOrderDetail.getPurchaseOrderDetail().getId());
            } catch (Exception e) {
                System.err.println("Error adding WorkOrderDetail for ProductionOrderDetail ID: " + detail.getId() + " - " + e.getMessage());
                throw e;
            }
        }
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
        System.err.println("Initialized WorkOrderDetail for ProductionOrderDetail ID: " + productionOrderDetail.getId());
        return detail;
    }

    /**
     * Adds a DyeStage to the WorkOrderDetail.
     */
    private void addDyeStage(WorkOrderDetail workOrderDetail, List<Long> dyeMachineIds, int dyeMachineIndex) {
        DyeStage dyeStage = initializeDyeStage(workOrderDetail);
        DyeMachine dyeMachine = selectDyeMachine(workOrderDetail, dyeMachineIds, dyeMachineIndex);

        BigDecimal[] dyeCalculations = calculateDyeStageValues(workOrderDetail, dyeMachine);
        BigDecimal coneWeight = dyeCalculations[0];
        BigDecimal coneBatchWeight = dyeCalculations[1];
        int dyeBatches = dyeCalculations[2].intValue();
        BigDecimal littersBatch = dyeCalculations[3];
        BigDecimal coneBatchQuantity = dyeCalculations[4];
        BigDecimal coneQuantity = dyeCalculations[5];
        BigDecimal litters = dyeCalculations[6];

        validateDyeStage(dyeMachine, littersBatch, coneBatchQuantity, workOrderDetail.getProductionOrderDetail().getId());

        dyeStage.setCone_weight(coneWeight);
        dyeStage.setCone_quantity(coneQuantity);
        dyeStage.setLiters(litters);
        dyeStage.setCone_batch_weight(coneBatchWeight);

        LocalDateTime dyeDeadline = calculateDyeDeadline(dyeStage.getPlannedStart(), dyeBatches);
        validateDeadline(dyeDeadline, workOrderDetail.getWorkOrder().getDeadline(), "Dye Stage",
                workOrderDetail.getProductionOrderDetail().getId());

        dyeStage.setDeadline(dyeDeadline);
        dyeStage.setDyeMachine(dyeMachine);
        dyeStage.setDyebatches(createDyeBatches(dyeStage, dyeMachine, dyeBatches, coneWeight, coneBatchWeight));
        assignTeamLeadersAndQAForDyeStage(dyeStage);
        workOrderDetail.setDyeStage(dyeStage);
        System.err.println("Added DyeStage for WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
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
            System.err.println("Error: Not enough Dye Machines selected for ProductionOrderDetail ID: " +
                    workOrderDetail.getProductionOrderDetail().getId());
            throw new IllegalArgumentException("Not enough Dye Machines selected for all WorkOrderDetails");
        }
        Long dyeMachineId = dyeMachineIds.get(index);
        //
        DyeMachine selectedMachine = dyeMachineRepository.findById(dyeMachineId)
                .orElseThrow(() -> new IllegalArgumentException("Dye Machine ID " + dyeMachineId + " not found"));
        System.err.println("Selected DyeMachine ID: " + dyeMachineId + " for ProductionOrderDetail ID: " +
                workOrderDetail.getProductionOrderDetail().getId());
        return selectedMachine;
    }

    /**
     * Calculates values for DyeStage (coneWeight, coneBatchWeight, dyeBatches, etc.).
     * Returns an array: [coneWeight, coneBatchWeight, dyeBatches, littersBatch, coneBatchQuantity, coneQuantity, litters]
     */
    private BigDecimal[] calculateDyeStageValues(WorkOrderDetail workOrderDetail, DyeMachine dyeMachine) {
        BigDecimal threadMass = workOrderDetail.getProductionOrderDetail().getThread_mass();
        if (threadMass == null || threadMass.equals(BigDecimal.ZERO)) {
            threadMass = workOrderDetail.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate()
                    .multiply(BigDecimal.valueOf(workOrderDetail.getPurchaseOrderDetail().getQuantity()));
            System.err.println("ThreadMass in production order is error or zero, recalculated: " + threadMass +
                    " for WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        }
        BigDecimal coneWeight = threadMass.add(BigDecimal.valueOf(0.4));
        BigDecimal maxWeight = BigDecimal.valueOf(dyeMachine.getMaxWeight());
        BigDecimal convertRate = workOrderDetail.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate();

        BigDecimal coneBatchWeight;
        BigDecimal dyeBatches;
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

        System.err.println("Calculated DyeStage values for WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null") +
                " - coneWeight: " + coneWeight + ", coneBatchWeight: " + coneBatchWeight + ", dyeBatches: " + dyeBatches);
        return new BigDecimal[]{coneWeight, coneBatchWeight, dyeBatches, littersBatch, coneBatchQuantity, coneQuantity, litters};
    }

    /**
     * Validates DyeStage constraints (littersBatch and coneBatchQuantity).
     */
    private void validateDyeStage(DyeMachine dyeMachine, BigDecimal littersBatch, BigDecimal coneBatchQuantity, Long detailId) {
        BigDecimal littersMin = dyeMachine.getLittersMin();
        BigDecimal littersMax = dyeMachine.getLittersMax();
        BigDecimal coneMin = dyeMachine.getConeMin();
        BigDecimal coneMax = dyeMachine.getConeMax();

        boolean isLittersBatchValid = littersBatch.compareTo(littersMin) >= 0 && littersBatch.compareTo(littersMax) <= 0;
        boolean isConeBatchQuantityValid = coneBatchQuantity.compareTo(coneMin) >= 0 && coneBatchQuantity.compareTo(coneMax) <= 0;

        if (!isLittersBatchValid || !isConeBatchQuantityValid) {
            StringBuilder errorMessage = new StringBuilder("Máy nhuộm không hợp lệ: ");
            if (!isLittersBatchValid) {
                errorMessage.append("Litters Batch (").append(littersBatch).append(") không nằm trong khoảng [")
                        .append(littersMin).append(", ").append(littersMax).append("]. ");
            }
            if (!isConeBatchQuantityValid) {
                errorMessage.append("Cone Batch Quantity (").append(coneBatchQuantity).append(") không nằm trong khoảng [")
                        .append(coneMin).append(", ").append(coneMax).append("].");
            }
            System.err.println("Error validating DyeStage for ProductionOrderDetail ID: " + detailId + " - " + errorMessage);
            throw new IllegalStateException(errorMessage.toString());
        }
    }

    /**
     * Calculates the deadline for DyeStage.
     */
    private LocalDateTime calculateDyeDeadline(LocalDateTime plannedStart, int dyeBatches) {
        long dyeDurationMinutes = dyeBatches * 120 + (dyeBatches - 1) * 15;
        LocalDateTime deadline = plannedStart.plusMinutes(dyeDurationMinutes);
        System.err.println("Calculated DyeStage deadline: " + deadline + " with " + dyeBatches + " batches");
        return deadline;
    }

    /**
     * Validates that a deadline does not exceed the WorkOrder deadline.
     */
    private void validateDeadline(LocalDateTime stageDeadline, LocalDate workOrderDeadline, String stageName, Long detailId) {
        if (stageDeadline.isAfter(workOrderDeadline.atTime(LocalTime.MAX))) {
            System.err.println("Error: " + stageName + " deadline " + stageDeadline +
                    " exceeds WorkOrder deadline " + workOrderDeadline + " for ProductionOrderDetail ID: " + detailId);
            throw new IllegalStateException(stageName + " deadline exceeds actual delivery date for detail " + detailId);
        }
    }

    /**
     * Creates DyeBatches for the DyeStage.
     */
    private List<DyeBatch> createDyeBatches(DyeStage dyeStage, DyeMachine dyeMachine, int dyeBatches,
                                            BigDecimal coneWeight, BigDecimal coneBatchWeight) {
        List<DyeBatch> dyeBatchList = new ArrayList<>();
        BigDecimal remainingConeWeight = coneWeight;
        for (int i = 0; i < dyeBatches; i++) {
            DyeBatch dyeBatch = new DyeBatch();
            dyeBatch.setWorkStatus(WorkEnum.NOT_STARTED);
            dyeBatch.setTestStatus(TestEnum.NOT_STARTED);
            dyeBatch.setCreateAt(LocalDateTime.now());
            BigDecimal currentConeBatchWeight = (i == dyeBatches - 1 && remainingConeWeight.compareTo(coneBatchWeight) < 0)
                    ? remainingConeWeight : coneBatchWeight;
            dyeBatch.setCone_batch_weight(currentConeBatchWeight);
            dyeBatch.setLiters_min(dyeMachine.getLittersMin());
            dyeBatch.setLiters(currentConeBatchWeight.multiply(BigDecimal.valueOf(6)));
            dyeBatch.setDyeStage(dyeStage);
            dyeBatch.setPlannedStart(dyeStage.getPlannedStart().plusMinutes(i * 135));
            dyeBatch.setDeadline(dyeBatch.getPlannedStart().plusMinutes(105));
            assignTeamLeaderAndQAForDyeBatch(dyeBatch);
            dyeBatchList.add(dyeBatch);
            remainingConeWeight = remainingConeWeight.subtract(currentConeBatchWeight);
            System.err.println("Created DyeBatch with cone_batch_weight: " + currentConeBatchWeight +
                    " for WorkOrderDetail ID: " + (dyeStage.getWorkOrderDetail().getId() != null ? dyeStage.getWorkOrderDetail().getId() : "null"));
        }
        return dyeBatchList;
    }

    /**
     * Adds a WindingStage to the WorkOrderDetail.
     */
    private void addWindingStage(WorkOrderDetail workOrderDetail, List<Long> windingMachineIds, int windingMachineIndex) {
        WindingStage windingStage = initializeWindingStage(workOrderDetail);
        WindingMachine windingMachine = selectWindingMachine(workOrderDetail, windingMachineIds, windingMachineIndex);

        int windingBatches = workOrderDetail.getDyeStage().getDyebatches().size();
        LocalDateTime windingDeadline = calculateWindingDeadline(windingStage.getPlannedStart(), windingBatches);
        validateDeadline(windingDeadline, workOrderDetail.getWorkOrder().getDeadline(), "Winding Stage",
                workOrderDetail.getProductionOrderDetail().getId());

        windingStage.setDeadline(windingDeadline);
        windingStage.setWindingMachine(windingMachine);
        windingStage.setWindingbatches(createWindingBatches(windingStage, windingBatches));
        assignTeamLeadersAndQAForWindingStage(windingStage);
        workOrderDetail.setWindingStage(windingStage);
        System.err.println("Added WindingStage for WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
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
        windingStage.setPlannedStart(dyeBatches > 1 ? workOrderDetail.getDyeStage().getPlannedStart().plusMinutes(270)
                : workOrderDetail.getDyeStage().getDeadline());
        windingStage.setWorkStatus(WorkEnum.NOT_STARTED);
        System.err.println("Initialized WindingStage for WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        return windingStage;
    }

    /**
     * Selects a WindingMachine from the available list.
     */
    private WindingMachine selectWindingMachine(WorkOrderDetail workOrderDetail, List<Long> windingMachineIds, int index) {
        if (index >= windingMachineIds.size()) {
            System.err.println("Error: Not enough Winding Machines selected for ProductionOrderDetail ID: " +
                    workOrderDetail.getProductionOrderDetail().getId());
            throw new IllegalArgumentException("Not enough Winding machines selected for all WorkOrderDetails");
        }
        Long windingMachineId = windingMachineIds.get(index);
        // Không cần kiểm tra lại tính khả dụng, vì đã kiểm tra trong createWorkOrder
        WindingMachine selectedMachine = windingMachineRepository.findById(windingMachineId)
                .orElseThrow(() -> new IllegalArgumentException("Winding Machine ID " + windingMachineId + " not found"));
        System.err.println("Selected WindingMachine ID: " + windingMachineId + " for ProductionOrderDetail ID: " +
                workOrderDetail.getProductionOrderDetail().getId());
        return selectedMachine;
    }

    /**
     * Calculates the deadline for WindingStage.
     */
    private LocalDateTime calculateWindingDeadline(LocalDateTime plannedStart, int windingBatches) {
        long windingDurationMinutes = windingBatches * 60 + (windingBatches - 1) * 15;
        LocalDateTime deadline = plannedStart.plusMinutes(windingDurationMinutes);
        System.err.println("Calculated WindingStage deadline: " + deadline + " with " + windingBatches + " batches");
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
            windingBatch.setCreateAt(LocalDateTime.now());
            windingBatch.setWindingStage(windingStage);
            windingBatch.setPlannedStart(windingStage.getPlannedStart().plusMinutes(i * 75));
            windingBatch.setDeadline(windingBatch.getPlannedStart().plusMinutes(60));
            assignTeamLeaderAndQAForWindingBatch(windingBatch);
            windingBatchList.add(windingBatch);
            System.err.println("Created WindingBatch for WorkOrderDetail ID: " + (windingStage.getWorkOrderDetail().getId() != null ? windingStage.getWorkOrderDetail().getId() : "null"));
        }
        return windingBatchList;
    }

    /**
     * Adds a PackagingStage to the WorkOrderDetail.
     */
    private void addPackagingStage(WorkOrderDetail workOrderDetail) {
        PackagingStage packagingStage = initializePackagingStage(workOrderDetail);
        int packagingBatches = workOrderDetail.getDyeStage().getDyebatches().size();

        BigDecimal totalPackagingDurationMinutes = calculatePackagingDuration(workOrderDetail, packagingBatches);
        LocalDateTime packagingDeadline = packagingStage.getPlannedStart().plusMinutes(totalPackagingDurationMinutes.longValue());
        validateDeadline(packagingDeadline, workOrderDetail.getWorkOrder().getDeadline(), "Packaging Stage",
                workOrderDetail.getProductionOrderDetail().getId());

        packagingStage.setDeadline(packagingDeadline);
        packagingStage.setPackagingBatches(createPackagingBatches(packagingStage, packagingBatches));
        assignTeamLeadersAndQAForPackagingStage(packagingStage);
        workOrderDetail.setPackagingStage(packagingStage);
        System.err.println("Added PackagingStage for WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
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
        packagingStage.setPlannedStart(dyeBatches > 1 ? workOrderDetail.getWindingStage().getPlannedStart().plusMinutes(150)
                : workOrderDetail.getWindingStage().getDeadline());
        packagingStage.setWorkStatus(WorkEnum.NOT_STARTED);
        System.err.println("Initialized PackagingStage for WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        return packagingStage;
    }

    /**
     * Calculates the total duration for PackagingStage.
     */
    private BigDecimal calculatePackagingDuration(WorkOrderDetail workOrderDetail, int packagingBatches) {
        BigDecimal packagingTimePerProduct = BigDecimal.valueOf(0.5);
        BigDecimal totalPackagingDurationMinutes = BigDecimal.ZERO;
        BigDecimal convertRate = workOrderDetail.getPurchaseOrderDetail().getProduct().getThread().getConvert_rate();
        for (DyeBatch dyeBatch : workOrderDetail.getDyeStage().getDyebatches()) {
            BigDecimal productsInBatch = dyeBatch.getCone_batch_weight().multiply(convertRate);
            totalPackagingDurationMinutes = totalPackagingDurationMinutes.add(productsInBatch.multiply(packagingTimePerProduct));
        }
        System.err.println("Calculated PackagingStage duration: " + totalPackagingDurationMinutes +
                " minutes for WorkOrderDetail ID: " + (workOrderDetail.getId() != null ? workOrderDetail.getId() : "null"));
        return totalPackagingDurationMinutes;
    }

    /**
     * Creates PackagingBatches for the PackagingStage.
     */
    private List<PackagingBatch> createPackagingBatches(PackagingStage packagingStage, int packagingBatches) {
        List<PackagingBatch> packagingBatchList = new ArrayList<>();
        WindingStage windingStage = packagingStage.getWorkOrderDetail().getWindingStage();
        List<WindingBatch> windingBatches = windingStage.getWindingbatches();

        BigDecimal convertRate = packagingStage.getWorkOrderDetail().getPurchaseOrderDetail().getProduct().getThread().getConvert_rate();
        BigDecimal packagingTimePerProduct = BigDecimal.valueOf(0.5);
        for (int i = 0; i < packagingBatches; i++) {
            PackagingBatch packagingBatch = new PackagingBatch();
            //
            WindingBatch windingBatch = windingBatches.get(i);
            packagingBatch.setWindingBatch(windingBatch);
            //
            packagingBatch.setCreateAt(LocalDateTime.now());
            packagingBatch.setPackagingStage(packagingStage);
            packagingBatch.setWorkStatus(WorkEnum.NOT_STARTED);
            packagingBatch.setTestStatus(TestEnum.NOT_STARTED);
            BigDecimal productsInBatch = packagingStage.getWorkOrderDetail().getDyeStage().getDyebatches().get(i)
                    .getCone_batch_weight().multiply(convertRate);
            long batchDurationMinutes = productsInBatch.multiply(packagingTimePerProduct).longValue();
            packagingBatch.setPlannedStart(i == 0 ? packagingStage.getPlannedStart()
                    : packagingBatchList.get(i - 1).getDeadline());
            packagingBatch.setDeadline(packagingBatch.getPlannedStart().plusMinutes(batchDurationMinutes));
            assignTeamLeaderAndQAForPackagingBatch(packagingBatch);
            packagingBatchList.add(packagingBatch);
            System.err.println("Created PackagingBatch for WorkOrderDetail ID: " + (packagingStage.getWorkOrderDetail().getId() != null ? packagingStage.getWorkOrderDetail().getId() : "null"));
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
                throw new IllegalArgumentException("Unknown stage type " + stageType);
        }
    }

    private User findTeamLeaderForShift(Shift shift, StageType stageType) {
        String requiredRole = getRoleForStageType(stageType);
        return shift.getUserShifts().stream().map(UserShift::getUser).filter(user -> user.getRole().getName().equals(requiredRole)).findFirst().orElseThrow(() -> new IllegalArgumentException("No team leader found for shift " + shift.getId()));
    }

    private User findQAForShift(Shift shift) {
        return shift.getUserShifts().stream().map(UserShift::getUser).
                filter(user -> user.getRole().getName().equals("QA")).findFirst().orElseThrow(()
                        -> new IllegalStateException("No QA found in shift " + shift.getShiftName()));
    }

    private Shift findShiftForTime(LocalTime time, LocalDate date) {
        List<Shift> shifts = shiftRepository.findAll();
        return shifts.stream().filter(shift -> {
            LocalTime shiftStart = shift.getShiftStart();
            LocalTime shiftEnd = shift.getShiftEnd();
            if (shiftEnd.isBefore(shiftStart)) { // Qua ngày, ví dụ: 0h-8h
                return (time.isAfter(shiftStart) && time.isBefore(LocalTime.MAX))
                        || (time.isBefore(shiftEnd) && time.isAfter(LocalTime.MIN));
            }
            return time.isAfter(shiftStart) && time.isBefore(shiftEnd);
        }).findFirst().orElseThrow(()
                -> new IllegalStateException("No shift found for time: " + time + " on " + date));
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
            User qa = findQAForShift(shift);
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
            batch.setQa(findQAForShift(endShift));
        } else {
            batch.setQa(findQAForShift(shift));
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
            User qa = findQAForShift(shift);
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
            batch.setQa(findQAForShift(endShift));
        } else {
            batch.setQa(findQAForShift(shift));
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
            User qa = findQAForShift(shift);
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
            batch.setQa(findQAForShift(endShift));
        } else {
            batch.setQa(findQAForShift(shift));
        }
    }
}