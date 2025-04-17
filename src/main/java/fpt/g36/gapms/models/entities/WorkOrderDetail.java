package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_order_detail")
public class WorkOrderDetail extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @NotNull
    private BigDecimal additionalWeight;

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @NotNull
    private LocalDateTime plannedStartAt;

    @NotNull
    private LocalDateTime plannedEndAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @OneToOne(mappedBy = "workOrderDetail", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DyeStage dyeStage;

    @OneToOne(mappedBy = "workOrderDetail", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private WindingStage windingStage;

    @OneToOne(mappedBy = "workOrderDetail", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PackagingStage packagingStage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_detail_id")
    private ProductionOrderDetail productionOrderDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_detail_id")
    private PurchaseOrderDetail purchaseOrderDetail;


    public WorkOrderDetail() {
    }

    public WorkOrderDetail(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrder workOrder, BigDecimal additionalWeight, LocalDateTime startAt, LocalDateTime completeAt, LocalDateTime plannedStartAt, LocalDateTime plannedEndAt, WorkEnum workStatus, DyeStage dyeStage, WindingStage windingStage, PackagingStage packagingStage, ProductionOrderDetail productionOrderDetail, PurchaseOrderDetail purchaseOrderDetail) {
        super(id, createAt, updateAt);
        this.workOrder = workOrder;
        this.additionalWeight = additionalWeight;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.plannedStartAt = plannedStartAt;
        this.plannedEndAt = plannedEndAt;
        this.workStatus = workStatus;
        this.dyeStage = dyeStage;
        this.windingStage = windingStage;
        this.packagingStage = packagingStage;
        this.productionOrderDetail = productionOrderDetail;
        this.purchaseOrderDetail = purchaseOrderDetail;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public BigDecimal getAdditionalWeight() {
        return additionalWeight;
    }

    public void setAdditionalWeight(BigDecimal additionalWeight) {
        this.additionalWeight = additionalWeight;
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

    public LocalDateTime getPlannedStartAt() {
        return plannedStartAt;
    }

    public void setPlannedStartAt(LocalDateTime plannedStartAt) {
        this.plannedStartAt = plannedStartAt;
    }

    public LocalDateTime getPlannedEndAt() {
        return plannedEndAt;
    }

    public void setPlannedEndAt(LocalDateTime plannedEndAt) {
        this.plannedEndAt = plannedEndAt;
    }

    public WorkEnum getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(WorkEnum workStatus) {
        this.workStatus = workStatus;
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