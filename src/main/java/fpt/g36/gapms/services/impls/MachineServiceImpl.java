package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.MachineType;
import fpt.g36.gapms.enums.StageType;
import fpt.g36.gapms.enums.WorkEnum;
import fpt.g36.gapms.models.entities.*;
import fpt.g36.gapms.repositories.*;
import fpt.g36.gapms.services.MachineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MachineServiceImpl implements MachineService {

    private final DyeMachineRepository dyeMachineRepository;
    private final WindingMachineRepository windingMachineRepository;
    private final MachineQueueRepository machineQueueRepository;
    private final DyeStageRepository dyeStageRepository;
    private final WindingStageRepository windingStageRepository;

    public MachineServiceImpl(DyeMachineRepository dyeMachineRepository,
                              WindingMachineRepository windingMachineRepository,
                              MachineQueueRepository machineQueueRepository, DyeStageRepository dyeStageRepository, WindingStageRepository windingStageRepository) {
        this.dyeMachineRepository = dyeMachineRepository;
        this.windingMachineRepository = windingMachineRepository;
        this.machineQueueRepository = machineQueueRepository;
        this.dyeStageRepository = dyeStageRepository;
        this.windingStageRepository = windingStageRepository;
    }

    @Override
    public Page<DyeMachine> getAllDyeMachines(Pageable pageable) {
        return dyeMachineRepository.findAllByOrderByCreateAtDesc(pageable);
    }

    @Override
    public DyeMachine getDyeMachinesById(Long id) {
        return dyeMachineRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Không tìm thấy máy nhuộm với ID: " + id));
    }

    @Override
    public Page<WindingMachine> getAllWindingMachines(Pageable pageable) {
        return windingMachineRepository.findAllByOrderByCreateAtDesc(pageable);
    }

    @Override
    public WindingMachine getWindingMachinesById(Long id) {
        return windingMachineRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Không tìm thấy máy cuốn với ID: " + id));
    }

    @Override
    public DyeMachine addDyeMachine(DyeMachine dyeMachine) {
        DyeMachine newDyeMachine = new DyeMachine();
        newDyeMachine.setDiameter(dyeMachine.getDiameter());
        newDyeMachine.setPile(dyeMachine.getPile());
        newDyeMachine.setConePerPile(dyeMachine.getConePerPile());
        newDyeMachine.setMaxWeight(dyeMachine.getMaxWeight());
        newDyeMachine.setCapacity(dyeMachine.getCapacity());
        newDyeMachine.setLittersMin(dyeMachine.getLittersMin());
        newDyeMachine.setLittersMax(dyeMachine.getLittersMax());
        newDyeMachine.setConeMin(dyeMachine.getConeMin());
        newDyeMachine.setConeMax(dyeMachine.getConeMax());
        newDyeMachine.setDescription(dyeMachine.getDescription() != null ? dyeMachine.getDescription() : "");
        newDyeMachine.setCreateAt(LocalDateTime.now());
        newDyeMachine.setUpdateAt(LocalDateTime.now());
        newDyeMachine.setDyeStage(null);
        return dyeMachineRepository.save(newDyeMachine);
    }

    @Override
    public WindingMachine addWindingMachine(WindingMachine windingMachine) {
        WindingMachine newWindingMachine = new WindingMachine();
        newWindingMachine.setMotor_speed(windingMachine.getMotor_speed());
        newWindingMachine.setSpindle(windingMachine.getSpindle());
        newWindingMachine.setCapacity(windingMachine.getCapacity());
        newWindingMachine.setDescription(windingMachine.getDescription() != null ? windingMachine.getDescription() : "");
        newWindingMachine.setCreateAt(LocalDateTime.now());
        newWindingMachine.setUpdateAt(LocalDateTime.now());
        newWindingMachine.setWindingStage(null);
        System.err.println(newWindingMachine);
        return windingMachineRepository.save(newWindingMachine);
    }

    @Override
    public DyeMachine updateDyeMachine(Long id, DyeMachine dyeMachine) {
        DyeMachine existingMachine = dyeMachineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy máy nhuộm với ID: " + id));
        if (existingMachine.getDyeStage() != null) {
            throw new IllegalStateException("Không thể cập nhật máy đã được gán vào stage.");
        }
        existingMachine.setDiameter(dyeMachine.getDiameter());
        existingMachine.setPile(dyeMachine.getPile());
        existingMachine.setConePerPile(dyeMachine.getConePerPile());
        existingMachine.setMaxWeight(dyeMachine.getMaxWeight());
        existingMachine.setCapacity(dyeMachine.getCapacity());
        existingMachine.setLittersMin(dyeMachine.getLittersMin());
        existingMachine.setLittersMax(dyeMachine.getLittersMax());
        existingMachine.setConeMin(dyeMachine.getConeMin());
        existingMachine.setConeMax(dyeMachine.getConeMax());
        existingMachine.setDescription(dyeMachine.getDescription() != null ? dyeMachine.getDescription() : "");
        existingMachine.setUpdateAt(LocalDateTime.now());
        existingMachine.setActive(dyeMachine.isActive());
        return dyeMachineRepository.save(existingMachine);
    }

    @Override
    public WindingMachine updateWindingMachine(Long id, WindingMachine windingMachine) {
        WindingMachine existingMachine = windingMachineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy máy cuốn với ID: " + id));
        if (existingMachine.getWindingStage() != null) {
            throw new IllegalStateException("Không thể cập nhật máy đã được gán vào stage.");
        }
        existingMachine.setMotor_speed(windingMachine.getMotor_speed());
        existingMachine.setSpindle(windingMachine.getSpindle());
        existingMachine.setCapacity(windingMachine.getCapacity());
        existingMachine.setDescription(windingMachine.getDescription() != null ? windingMachine.getDescription() : "");
        existingMachine.setUpdateAt(LocalDateTime.now());
        existingMachine.setActive(windingMachine.isActive());
        return windingMachineRepository.save(existingMachine);
    }

    @Override
    public void updateDyeMachineStatus(Long id, boolean isActive) {
        DyeMachine dyeMachine = dyeMachineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy máy nhuộm với ID: " + id));
        dyeMachine.setActive(isActive);
        dyeMachine.setUpdateAt(LocalDateTime.now());
        dyeMachineRepository.save(dyeMachine);
    }

    @Override
    public void updateWindingMachineStatus(Long id, boolean isActive) {
        WindingMachine windingMachine = windingMachineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy máy cuốn với ID: " + id));
        windingMachine.setActive(isActive);
        windingMachine.setUpdateAt(LocalDateTime.now());
        windingMachineRepository.save(windingMachine);
    }

    @Override
    @Transactional
    public List<DyeMachine> findAvailableDyeMachines(WorkOrderDetail workOrderDetail,
                                                     LocalDateTime plannedStart,
                                                     LocalDateTime deadline) {
        List<DyeMachine> activeMachines = dyeMachineRepository.findDyeMachineByActiveIsTrue();
        List<DyeMachine> availableMachines = new ArrayList<>();
        LocalDateTime actualDeliveryDate = workOrderDetail.getWorkOrder()
                .getProductionOrder().getPurchaseOrder().getSolution().getActualDeliveryDate().atStartOfDay();

        for (DyeMachine machine : activeMachines) {
            // Kiểm tra xem máy có đang được sử dụng bởi bất kỳ DyeStage nào không
            List<DyeStage> activeDyeStages = dyeStageRepository.findActiveDyeStagesByMachine(
                    machine.getId(), plannedStart, deadline, WorkEnum.FINISHED);

            // Máy được coi là rảnh nếu không có DyeStage nào đang sử dụng nó
            boolean isMachineFree = activeDyeStages.isEmpty();

            // Kiểm tra hàng đợi machine_queue
            List<MachineQueue> overlappingQueues = machineQueueRepository.findOverlappingQueue(
                    machine.getId(), plannedStart, deadline, deadline);

            // Kiểm tra thời hạn giao hàng
            boolean meetsDeliveryDate = deadline.isBefore(actualDeliveryDate);

            if (isMachineFree && overlappingQueues.isEmpty() && meetsDeliveryDate) {
                availableMachines.add(machine);
            }
        }
        return availableMachines;
    }

    @Override
    @Transactional
    public List<WindingMachine> findAvailableWindingMachines(WorkOrderDetail workOrderDetail,
                                                             LocalDateTime plannedStart,
                                                             LocalDateTime deadline) {
        List<WindingMachine> activeMachines = windingMachineRepository.findWindingMachineByActiveIsTrue();
        List<WindingMachine> availableMachines = new ArrayList<>();
        LocalDateTime actualDeliveryDate = workOrderDetail.getWorkOrder()
                .getProductionOrder().getPurchaseOrder().getSolution().getActualDeliveryDate().atStartOfDay();

        for (WindingMachine machine : activeMachines) {
            // Kiểm tra xem máy có đang được sử dụng bởi bất kỳ WindingStage nào không
            List<WindingStage> activeWindingStages = windingStageRepository.findActiveWindingStagesByMachine(
                    machine.getId(), plannedStart, deadline, WorkEnum.FINISHED);

            // Máy được coi là rảnh nếu không có WindingStage nào đang sử dụng nó
            boolean isMachineFree = activeWindingStages.isEmpty();

            // Kiểm tra hàng đợi machine_queue
            List<MachineQueue> overlappingQueues = machineQueueRepository.findOverlappingQueue(
                    machine.getId(), plannedStart, deadline, deadline);

            // Kiểm tra thời hạn giao hàng
            boolean meetsDeliveryDate = deadline.isBefore(actualDeliveryDate);

            if (isMachineFree && overlappingQueues.isEmpty() && meetsDeliveryDate) {
                availableMachines.add(machine);
            }
        }
        return availableMachines;
    }

    @Override
    @Transactional
    public void addToMachineQueue(WorkOrderDetail workOrderDetail, Long machineId,
                                  LocalDateTime plannedStart, LocalDateTime deadline,
                                  StageType stageType, MachineType machineType) {
        boolean isMachineValid;
        if (machineType == MachineType.DYE_MACHINE) {
            isMachineValid = dyeMachineRepository.findById(machineId).isPresent();
        } else if (machineType == MachineType.WINDING_MACHINE) {
            isMachineValid = windingMachineRepository.findById(machineId).isPresent();
        } else {
            throw new IllegalArgumentException("Invalid machine type: " + machineType);
        }

        if (!isMachineValid) {
            throw new IllegalArgumentException("Machine with id: " + machineId + " does not exist or has invalid machine type: " + machineType);
        }

        // Kiểm tra trùng thời gian cho máy cụ thể
        List<MachineQueue> overlappingQueues = machineQueueRepository.findOverlappingQueue(
                machineId, plannedStart, deadline, deadline);
        if (!overlappingQueues.isEmpty()) {
            throw new IllegalStateException("Machine " + machineId + " already has a queue in this time range");
        }

        // Kiểm tra hàng đợi đầy cho loại máy
        long machinesInQueue = machineQueueRepository.countDistinctMachinesInQueueByType(machineType, plannedStart, deadline);
        long totalMachines = (machineType == MachineType.DYE_MACHINE)
                ? dyeMachineRepository.count()
                : windingMachineRepository.count();
        if (machinesInQueue >= totalMachines) {
            throw new IllegalStateException("All " + machineType + " machines are in queue for this time range");
        }

        try {
            MachineQueue machineQueue = new MachineQueue(machineId, workOrderDetail, plannedStart, deadline, stageType, machineType);
            machineQueueRepository.save(machineQueue);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new IllegalStateException("Failed to add to queue due to concurrent modification for machine: " + machineId, e);
        }
    }

    private List<Object> findMachinesWithEarliestDeadline(MachineType machineType) {
        List<MachineQueue> earliestQueues = machineQueueRepository.findQueuesWithEarliestDeadline(machineType, LocalDateTime.now());
        List<Object> machines = new ArrayList<>();

        for (MachineQueue machineQueue : earliestQueues) {
            Long machineId = machineQueue.getMachineId();
            if (machineType == MachineType.DYE_MACHINE) {
                dyeMachineRepository.findById(machineId).ifPresent(machines::add);
            } else if (machineType == MachineType.WINDING_MACHINE) {
                windingMachineRepository.findById(machineId).ifPresent(machines::add);
            }
        }
        return machines;
    }

    // Phương thức hỗ trợ MVC để kiểm tra hàng đợi đầy
    public boolean isQueueFullForMachineType(MachineType machineType, LocalDateTime plannedStart, LocalDateTime deadline) {
        long machinesInQueue = machineQueueRepository.countDistinctMachinesInQueueByType(machineType, plannedStart, deadline);
        long totalMachines = (machineType == MachineType.DYE_MACHINE)
                ? dyeMachineRepository.count()
                : windingMachineRepository.count();
        return machinesInQueue >= totalMachines;
    }

    // Phương thức hỗ trợ MVC để lấy danh sách máy khả dụng
    public List<DyeMachine> getAvailableDyeMachinesForSelection(WorkOrderDetail workOrderDetail,
                                                                LocalDateTime plannedStart,
                                                                LocalDateTime deadline) {
        return findAvailableDyeMachines(workOrderDetail, plannedStart, deadline);
    }

    public List<WindingMachine> getAvailableWindingMachinesForSelection(WorkOrderDetail workOrderDetail,
                                                                        LocalDateTime plannedStart,
                                                                        LocalDateTime deadline) {
        return findAvailableWindingMachines(workOrderDetail, plannedStart, deadline);
    }

    //
    @Override
    @Transactional
    public List<DyeMachine> findFreeDyeMachines(LocalDateTime plannedStart, LocalDateTime deadline) {
        List<DyeMachine> activeMachines = dyeMachineRepository.findDyeMachineByActiveIsTrue();
        List<DyeMachine> freeMachines = new ArrayList<>();

        for (DyeMachine machine : activeMachines) {
            // Kiểm tra xem máy có trong hàng đợi trong khoảng thời gian plannedStart -> deadline không
            List<MachineQueue> overlappingQueues = machineQueueRepository.findOverlappingQueue(
                    machine.getId(), plannedStart, deadline, deadline);

            // Kiểm tra xem máy có đang được sử dụng bởi DyeStage nào không
            List<DyeStage> activeDyeStages = dyeStageRepository.findActiveDyeStagesByMachine(
                    machine.getId(), plannedStart, deadline, WorkEnum.FINISHED);

            // Máy được coi là rảnh nếu không có hàng đợi chồng lấn và không có DyeStage đang sử dụng
            if (overlappingQueues.isEmpty() && activeDyeStages.isEmpty()) {
                freeMachines.add(machine);
            }
        }
        return freeMachines;
    }

    @Override
    @Transactional
    public List<DyeMachine> findQueuedDyeMachinesForProductionOrder(ProductionOrder productionOrder,
                                                                    LocalDateTime plannedStart,
                                                                    LocalDateTime deadline) {
        List<DyeMachine> activeMachines = dyeMachineRepository.findDyeMachineByActiveIsTrue();
        List<DyeMachine> queuedMachines = new ArrayList<>();
        LocalDateTime actualDeliveryDate = productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate().atStartOfDay();

        for (DyeMachine machine : activeMachines) {
            List<MachineQueue> overlappingQueues = machineQueueRepository.findOverlappingQueue(
                    machine.getId(), plannedStart, deadline, deadline);
            List<MachineQueue> queues = machineQueueRepository.findByMachineId(machine.getId());
            boolean meetsDeliveryDate = deadline.isBefore(actualDeliveryDate);

            // Kiểm tra xem máy có đang được sử dụng bởi DyeStage nào không
            List<DyeStage> activeDyeStages = dyeStageRepository.findActiveDyeStagesByMachine(
                    machine.getId(), plannedStart, deadline, WorkEnum.FINISHED);

            // Máy được coi là khả dụng nếu có trong hàng đợi, không chồng lấn, không có DyeStage đang sử dụng, và thỏa mãn thời gian giao hàng
            if (!queues.isEmpty() && overlappingQueues.isEmpty() && activeDyeStages.isEmpty() && meetsDeliveryDate) {
                queuedMachines.add(machine);
            }
        }
        return queuedMachines;
    }

    @Override
    @Transactional
    public List<WindingMachine> findFreeWindingMachines(LocalDateTime plannedStart, LocalDateTime deadline) {
        List<WindingMachine> activeMachines = windingMachineRepository.findWindingMachineByActiveIsTrue();
        List<WindingMachine> freeMachines = new ArrayList<>();

        for (WindingMachine machine : activeMachines) {
            // Kiểm tra xem máy có trong hàng đợi trong khoảng thời gian plannedStart -> deadline không
            List<MachineQueue> overlappingQueues = machineQueueRepository.findOverlappingQueue(
                    machine.getId(), plannedStart, deadline, deadline);

            // Kiểm tra xem máy có đang được sử dụng bởi WindingStage nào không
            List<WindingStage> activeWindingStages = windingStageRepository.findActiveWindingStagesByMachine(
                    machine.getId(), plannedStart, deadline, WorkEnum.FINISHED);

            // Máy được coi là rảnh nếu không có hàng đợi chồng lấn và không có WindingStage đang sử dụng
            if (overlappingQueues.isEmpty() && activeWindingStages.isEmpty()) {
                freeMachines.add(machine);
            }
        }
        return freeMachines;
    }

    @Override
    @Transactional
    public List<WindingMachine> findQueuedWindingMachinesForProductionOrder(ProductionOrder productionOrder,
                                                                            LocalDateTime plannedStart,
                                                                            LocalDateTime deadline) {
        List<WindingMachine> activeMachines = windingMachineRepository.findWindingMachineByActiveIsTrue();
        List<WindingMachine> queuedMachines = new ArrayList<>();
        LocalDateTime actualDeliveryDate = productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate().atStartOfDay();

        for (WindingMachine machine : activeMachines) {
            List<MachineQueue> overlappingQueues = machineQueueRepository.findOverlappingQueue(
                    machine.getId(), plannedStart, deadline, deadline);
            List<MachineQueue> queues = machineQueueRepository.findByMachineId(machine.getId());
            boolean meetsDeliveryDate = deadline.isBefore(actualDeliveryDate);

            // Kiểm tra xem máy có đang được sử dụng bởi WindingStage nào không
            List<WindingStage> activeWindingStages = windingStageRepository.findActiveWindingStagesByMachine(
                    machine.getId(), plannedStart, deadline, WorkEnum.FINISHED);

            // Máy được coi là khả dụng nếu có trong hàng đợi, không chồng lấn, không có WindingStage đang sử dụng, và thỏa mãn thời gian giao hàng
            if (!queues.isEmpty() && overlappingQueues.isEmpty() && activeWindingStages.isEmpty() && meetsDeliveryDate) {
                queuedMachines.add(machine);
            }
        }
        return queuedMachines;
    }

    @Override
    @Transactional
    public List<DyeMachine> findAvailableDyeMachinesForProductionOrder(ProductionOrder productionOrder,
                                                                       LocalDateTime plannedStart,
                                                                       LocalDateTime deadline) {
        List<DyeMachine> activeMachines = dyeMachineRepository.findDyeMachineByActiveIsTrue();
        List<DyeMachine> availableMachines = new ArrayList<>();
        LocalDateTime actualDeliveryDate = productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate().atStartOfDay();

        for (DyeMachine machine : activeMachines) {
            // Kiểm tra xem máy có bị chiếm dụng trong khoảng thời gian plannedStart -> deadline không
            List<MachineQueue> overlappingQueues = machineQueueRepository.findOverlappingQueue(
                    machine.getId(), plannedStart, deadline, deadline);

            // Kiểm tra xem máy có đang được sử dụng bởi DyeStage nào không
            List<DyeStage> activeDyeStages = dyeStageRepository.findActiveDyeStagesByMachine(
                    machine.getId(), plannedStart, deadline, WorkEnum.FINISHED);

            // Điều kiện: Không có hàng đợi chồng lấn, không có DyeStage đang sử dụng, và deadline trước ngày giao hàng
            boolean meetsDeliveryDate = deadline.isBefore(actualDeliveryDate);

            if (overlappingQueues.isEmpty() && activeDyeStages.isEmpty() && meetsDeliveryDate) {
                availableMachines.add(machine);
            }
        }
        return availableMachines;
    }

    @Override
    @Transactional
    public List<WindingMachine> findAvailableWindingMachinesForProductionOrder(ProductionOrder productionOrder,
                                                                               LocalDateTime plannedStart,
                                                                               LocalDateTime deadline) {
        List<WindingMachine> activeMachines = windingMachineRepository.findWindingMachineByActiveIsTrue();
        List<WindingMachine> availableMachines = new ArrayList<>();
        LocalDateTime actualDeliveryDate = productionOrder.getPurchaseOrder().getSolution().getActualDeliveryDate().atStartOfDay();

        for (WindingMachine machine : activeMachines) {
            // Kiểm tra xem máy có bị chiếm dụng trong khoảng thời gian plannedStart -> deadline không
            List<MachineQueue> overlappingQueues = machineQueueRepository.findOverlappingQueue(
                    machine.getId(), plannedStart, deadline, deadline);

            // Kiểm tra xem máy có đang được sử dụng bởi WindingStage nào không
            List<WindingStage> activeWindingStages = windingStageRepository.findActiveWindingStagesByMachine(
                    machine.getId(), plannedStart, deadline, WorkEnum.FINISHED);

            // Điều kiện: Không có hàng đợi chồng lấn, không có WindingStage đang sử dụng, và deadline trước ngày giao hàng
            boolean meetsDeliveryDate = deadline.isBefore(actualDeliveryDate);

            if (overlappingQueues.isEmpty() && activeWindingStages.isEmpty() && meetsDeliveryDate) {
                availableMachines.add(machine);
            }
        }
        return availableMachines;
    }
}