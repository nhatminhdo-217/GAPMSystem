package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "packaging_stage")
public class PackagingStage extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "work_order_detail_id", nullable = false)
    private WorkOrderDetail workOrderDetail;

    @OneToOne
    @JoinColumn(name = "winding_stage_id", nullable = false)
    private WindingStage windingStage;

    @NotNull
    private LocalDateTime receivedProductAt;

    @NotNull
    private LocalDate deadline;

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @OneToMany(mappedBy = "packagingStage", fetch = FetchType.LAZY)
    private List<PackagingBatch> packagingBatches;

    public PackagingStage() {
    }

    public PackagingStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrderDetail workOrderDetail, WindingStage windingStage, LocalDateTime receivedProductAt, LocalDate deadline, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, List<PackagingBatch> packagingBatches) {
        super(id, createAt, updateAt);
        this.workOrderDetail = workOrderDetail;
        this.windingStage = windingStage;
        this.receivedProductAt = receivedProductAt;
        this.deadline = deadline;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.packagingBatches = packagingBatches;
    }

    public WorkOrderDetail getWorkOrderDetail() {
        return workOrderDetail;
    }

    public void setWorkOrderDetail(WorkOrderDetail workOrderDetail) {
        this.workOrderDetail = workOrderDetail;
    }

    public WindingStage getWindingStage() {
        return windingStage;
    }

    public void setWindingStage(WindingStage windingStage) {
        this.windingStage = windingStage;
    }

    public LocalDateTime getReceivedProductAt() {
        return receivedProductAt;
    }

    public void setReceivedProductAt(LocalDateTime receivedProductAt) {
        this.receivedProductAt = receivedProductAt;
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

    public List<PackagingBatch> getPackagingBatches() {
        return packagingBatches;
    }

    public void setPackagingBatches(List<PackagingBatch> packagingBatches) {
        this.packagingBatches = packagingBatches;
    }
}
