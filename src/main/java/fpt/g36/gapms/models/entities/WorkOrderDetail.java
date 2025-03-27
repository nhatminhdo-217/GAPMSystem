package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @OneToOne(mappedBy = "workOrderDetail")
    private DyeStage dyeStage;

    @OneToOne(mappedBy = "workOrderDetail")
    private WindingStage windingStage;

    @OneToOne(mappedBy = "workOrderDetail")
    private PackagingStage packagingStage;

    @OneToOne
    @JoinColumn(name = "production_order_detail_id")
    private ProductionOrderDetail productionOrderDetail;

    @OneToOne
    @JoinColumn(name = "purchase_order_detail_id")
    private PurchaseOrderDetail purchaseOrderDetail;


    public WorkOrderDetail() {
    }

    public WorkOrderDetail(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrder workOrder, LocalDateTime startAt, LocalDateTime completeAt, DyeStage dyeStage, WindingStage windingStage, PackagingStage packagingStage, WorkEnum workStatus, ProductionOrderDetail productionOrderDetail, PurchaseOrderDetail purchaseOrderDetail) {
        super(id, createAt, updateAt);
        this.workOrder = workOrder;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.dyeStage = dyeStage;
        this.windingStage = windingStage;
        this.packagingStage = packagingStage;
        this.workStatus = workStatus;
        this.productionOrderDetail = productionOrderDetail;
        this.purchaseOrderDetail = purchaseOrderDetail;
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

    public WorkEnum getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(WorkEnum workStatus) {
        this.workStatus = workStatus;
    }

    public ProductionOrderDetail getProductionOrderDetail() {
        return productionOrderDetail;
    }

    public void setProductionOrderDetail(ProductionOrderDetail productionOrderDetail) {
        this.productionOrderDetail = productionOrderDetail;
    }

    public PurchaseOrderDetail getPurchaseOrderDetail() {
        return purchaseOrderDetail;
    }

    public void setPurchaseOrderDetail(PurchaseOrderDetail purchaseOrderDetail) {
        this.purchaseOrderDetail = purchaseOrderDetail;
    }
}