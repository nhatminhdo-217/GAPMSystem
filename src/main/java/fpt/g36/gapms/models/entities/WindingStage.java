package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "winding_stage")
public class WindingStage extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "work_order_detail_id", nullable = false)
    private WorkOrderDetail workOrderDetail;

    @OneToOne
    @JoinColumn(name = "dye_stage_id", nullable = false)
    private DyeStage dyeStage; //Lấy ngày kết thúc nhuộm và khối lượng nhuộm

    @NotNull
    private LocalDateTime receivedConeAt; //Thời gian sợi về


    private LocalDate plannedStart;

    @NotNull
    private LocalDate deadline;

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @OneToOne
    @JoinColumn(name = "winding_machine_id", nullable = false)
    private WindingMachine windingMachine;

    @OneToOne(mappedBy = "windingStage")
    private PackagingStage packagingStage;

    @OneToMany(mappedBy = "windingStage", fetch = FetchType.LAZY)
    private List<WindingBatch> windingbatches;

    public WindingStage() {
    }


    public WindingStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrderDetail workOrderDetail, DyeStage dyeStage, LocalDateTime receivedConeAt, LocalDate plannedStart, LocalDate deadline, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, WindingMachine windingMachine, PackagingStage packagingStage, List<WindingBatch> windingbatches) {
        super(id, createAt, updateAt);
        this.workOrderDetail = workOrderDetail;
        this.dyeStage = dyeStage;
        this.receivedConeAt = receivedConeAt;
        this.plannedStart = plannedStart;
        this.deadline = deadline;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.windingMachine = windingMachine;
        this.packagingStage = packagingStage;
        this.windingbatches = windingbatches;
    }

    public LocalDate getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDate plannedStart) {
        this.plannedStart = plannedStart;
    }

    public WorkOrderDetail getWorkOrderDetail() {
        return workOrderDetail;
    }

    public void setWorkOrderDetail(WorkOrderDetail workOrderDetail) {
        this.workOrderDetail = workOrderDetail;
    }

    public DyeStage getDyeStage() {
        return dyeStage;
    }

    public void setDyeStage(DyeStage dyeStage) {
        this.dyeStage = dyeStage;
    }

    public LocalDateTime getReceivedConeAt() {
        return receivedConeAt;
    }

    public void setReceivedConeAt(LocalDateTime receivedConeAt) {
        this.receivedConeAt = receivedConeAt;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getCompleteAt() {
        return completeAt;
    }

    public void setCompleteAt(LocalDateTime completeAt) {
        this.completeAt = completeAt;
    }

    public WorkEnum getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(WorkEnum workStatus) {
        this.workStatus = workStatus;
    }

    public WindingMachine getWindingMachine() {
        return windingMachine;
    }

    public void setWindingMachine(WindingMachine windingMachine) {
        this.windingMachine = windingMachine;
    }

    public PackagingStage getPackagingStage() {
        return packagingStage;
    }

    public void setPackagingStage(PackagingStage packagingStage) {
        this.packagingStage = packagingStage;
    }

    public List<WindingBatch> getWindingbatches() {
        return windingbatches;
    }

    public void setWindingbatches(List<WindingBatch> windingbatches) {
        this.windingbatches = windingbatches;
    }
}
