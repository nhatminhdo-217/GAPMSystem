package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.BaseEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "production_order")
public class ProductionOrder extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BaseEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @OneToMany(mappedBy = "productionOrder")
    private List<ProductionOrderDetail> productionOrderDetails;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @OneToOne(mappedBy = "productionOrder")
    private WorkOrder workOrder;

    public ProductionOrder() {
    }

    public ProductionOrder(Long id, LocalDateTime createAt, LocalDateTime updateAt, BaseEnum status, User createdBy, User approvedBy, List<ProductionOrderDetail> productionOrderDetails, PurchaseOrder purchaseOrder, WorkOrder workOrder) {
        super(id, createAt, updateAt);
        this.status = status;
        this.createdBy = createdBy;
        this.approvedBy = approvedBy;
        this.productionOrderDetails = productionOrderDetails;
        this.purchaseOrder = purchaseOrder;
        this.workOrder = workOrder;
    }

    public BaseEnum getStatus() {
        return status;
    }

    public void setStatus(BaseEnum status) {
        this.status = status;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public List<ProductionOrderDetail> getProductionOrderDetails() {
        return productionOrderDetails;
    }

    public void setProductionOrderDetails(List<ProductionOrderDetail> productionOrderDetails) {
        this.productionOrderDetails = productionOrderDetails;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }
}