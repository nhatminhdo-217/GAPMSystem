package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.DyeMachine;
import fpt.g36.gapms.models.entities.WindingMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface MachineService {
    Page<DyeMachine> getAllDyeMachines(Pageable pageable);

    DyeMachine getDyeMachinesById(Long id);

    Page<WindingMachine> getAllWindingMachines(Pageable pageable);

    WindingMachine getWindingMachinesById(Long id);

    DyeMachine addDyeMachine(DyeMachine dyeMachine);

    WindingMachine addWindingMachine(WindingMachine windingMachine);

    Page<DyeMachine> getDyeMachinesWithNoStageInAmmountOfTime(LocalDate plannedStart, LocalDate plannedEnd, Pageable pageable);

    Page<WindingMachine> getWindingMachinesWithNoStage(Pageable pageable);

    DyeMachine updateDyeMachine(Long id, DyeMachine dyeMachine);

    WindingMachine updateWindingMachine(Long id, WindingMachine windingMachine);

    void updateDyeMachineStatus(Long id, boolean isActive);

    void updateWindingMachineStatus(Long id, boolean isActive);
}
