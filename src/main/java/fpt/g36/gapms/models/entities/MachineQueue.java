package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.MachineType;
import fpt.g36.gapms.enums.StageType;
import fpt.g36.gapms.repositories.DyeMachineRepository;
import fpt.g36.gapms.repositories.WindingMachineRepository;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "machine_queue")
public class MachineQueue extends BaseEntity {

    @Column(name = "machine_id", nullable = false)
    private Long machineId;

    @ManyToOne
    @JoinColumn(name = "work_order_detail_id", nullable = false)
    private WorkOrderDetail workOrderDetail;

    @Column(name = "planned_start", nullable = false)
    private LocalDateTime plannedStart;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "stage_type", nullable = false)
    private StageType stageType;

    @Column(name = "machine_type", nullable = false)
    private MachineType machineType;

    @Version
    private long version;

    public MachineQueue() {
    }

    public MachineQueue(Long id, LocalDateTime createAt, LocalDateTime updateAt, Long machineId, WorkOrderDetail workOrderDetail, LocalDateTime plannedStart, LocalDateTime deadline, StageType stageType, MachineType machineType, long version) {
        super(id, createAt, updateAt);
        this.machineId = machineId;
        this.workOrderDetail = workOrderDetail;
        this.plannedStart = plannedStart;
        this.deadline = deadline;
        this.stageType = stageType;
        this.machineType = machineType;
        this.version = version;
    }

    public MachineQueue(Long machineId, WorkOrderDetail workOrderDetail, LocalDateTime plannedStart, LocalDateTime deadline, StageType stageType, MachineType machineType) {
        this.machineId = machineId;
        this.workOrderDetail = workOrderDetail;
        this.plannedStart = plannedStart;
        this.deadline = deadline;
        this.stageType = stageType;
        this.machineType = machineType;
        validateMachineAndStage();
    }

    private void validateMachineAndStage() {
        if (stageType == StageType.DYE && machineType != MachineType.DYE_MACHINE) {
            throw new IllegalArgumentException("Machine type must be DYE_MACHINE for stage type DYE");
        } else if (stageType == StageType.WINDING && machineType != MachineType.WINDING_MACHINE) {
            throw new IllegalArgumentException("Machine type must be WINDING_MACHINE for stage type WINDING");
        }

        if (stageType == StageType.DYE) {
            DyeStage dyeStage = workOrderDetail.getDyeStage();
            if (dyeStage != null && dyeStage.getDyeMachine() != null && !machineId.equals(dyeStage.getDyeMachine().getId())) {
                throw new IllegalArgumentException("Machine ID does not match with machine in DyeStage");
            }
        } else if (stageType == StageType.WINDING) {
            WindingStage windingStage = workOrderDetail.getWindingStage();
            if (windingStage != null && windingStage.getWindingMachine() != null && !machineId.equals(windingStage.getWindingMachine().getId())) {
                throw new IllegalArgumentException("Machine ID does not match with machine in WindingStage");
            }
        }
    }

    public boolean isValidForStage(DyeMachineRepository dyeMachineRepository, WindingMachineRepository windingMachineRepository) {
        if (machineType == MachineType.DYE_MACHINE) {
            return dyeMachineRepository.findById(machineId).isPresent();
        } else if (machineType == MachineType.WINDING_MACHINE) {
            return windingMachineRepository.findById(machineId).isPresent();
        }
        return false;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public WorkOrderDetail getWorkOrderDetail() {
        return workOrderDetail;
    }

    public void setWorkOrderDetail(WorkOrderDetail workOrderDetail) {
        this.workOrderDetail = workOrderDetail;
    }

    public LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public StageType getStageType() {
        return stageType;
    }

    public void setStageType(StageType stageType) {
        this.stageType = stageType;
    }

    public MachineType getMachineType() {
        return machineType;
    }

    public void setMachineType(MachineType machineType) {
        this.machineType = machineType;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}