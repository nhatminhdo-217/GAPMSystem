package fpt.g36.gapms.services;

import fpt.g36.gapms.enums.MachineType;
import fpt.g36.gapms.enums.StageType;
import fpt.g36.gapms.models.entities.DyeMachine;
import fpt.g36.gapms.models.entities.ProductionOrder;
import fpt.g36.gapms.models.entities.WindingMachine;
import fpt.g36.gapms.models.entities.WorkOrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface MachineService {
    Page<DyeMachine> getAllDyeMachines(Pageable pageable);

    DyeMachine getDyeMachinesById(Long id);

    Page<WindingMachine> getAllWindingMachines(Pageable pageable);

    WindingMachine getWindingMachinesById(Long id);

    DyeMachine addDyeMachine(DyeMachine dyeMachine);

    WindingMachine addWindingMachine(WindingMachine windingMachine);

    DyeMachine updateDyeMachine(Long id, DyeMachine dyeMachine);

    WindingMachine updateWindingMachine(Long id, WindingMachine windingMachine);

    void updateDyeMachineStatus(Long id, boolean isActive);

    void updateWindingMachineStatus(Long id, boolean isActive);

    List<DyeMachine> findAvailableDyeMachines(WorkOrderDetail workOrderDetail, LocalDateTime plannedStart, LocalDateTime deadline);

    List<WindingMachine> findAvailableWindingMachines(WorkOrderDetail workOrderDetail, LocalDateTime plannedStart, LocalDateTime deadline);

    void addToMachineQueue(WorkOrderDetail workOrderDetail, Long machineId, LocalDateTime plannedStart, LocalDateTime deadline, StageType stageType, MachineType machineType);

    List<DyeMachine> findFreeDyeMachines(LocalDateTime plannedStart, LocalDateTime deadline);

    List<DyeMachine> findQueuedDyeMachinesForProductionOrder(ProductionOrder productionOrder, LocalDateTime plannedStart, LocalDateTime deadline);

    List<WindingMachine> findFreeWindingMachines(LocalDateTime plannedStart, LocalDateTime deadline);

    List<WindingMachine> findQueuedWindingMachinesForProductionOrder(ProductionOrder productionOrder, LocalDateTime plannedStart, LocalDateTime deadline);

    List<DyeMachine> findAvailableDyeMachinesForProductionOrder(ProductionOrder productionOrder,
                                                                LocalDateTime plannedStart,
                                                                LocalDateTime deadline);

    List<WindingMachine> findAvailableWindingMachinesForProductionOrder(ProductionOrder productionOrder,
                                                                        LocalDateTime plannedStart,
                                                                        LocalDateTime deadline);
}

