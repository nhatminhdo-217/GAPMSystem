package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_order_detail")
public class WorkOrderDetail extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @OneToOne(mappedBy = "workOrderDetail")
    private DyeStage dyeStage;

    @OneToOne(mappedBy = "workOrderDetail")
    private WindingStage windingStage;

    @OneToOne(mappedBy = "workOrderDetail")
    private PackagingStage packagingStage;

    public WorkOrderDetail() {
    }

    public WorkOrderDetail(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrder workOrder, LocalDateTime startAt, LocalDateTime completeAt, DyeStage dyeStage, WindingStage windingStage, PackagingStage packagingStage) {
        super(id, createAt, updateAt);
        this.workOrder = workOrder;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.dyeStage = dyeStage;
        this.windingStage = windingStage;
        this.packagingStage = packagingStage;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
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

    public DyeStage getDyeStage() {
        return dyeStage;
    }

    public void setDyeStage(DyeStage dyeStage) {
        this.dyeStage = dyeStage;
    }

    public WindingStage getWindingStage() {
        return windingStage;
    }

    public void setWindingStage(WindingStage windingStage) {
        this.windingStage = windingStage;
    }

    public PackagingStage getPackagingStage() {
        return packagingStage;
    }

    public void setPackagingStage(PackagingStage packagingStage) {
        this.packagingStage = packagingStage;
    }
}