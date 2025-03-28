package fpt.g36.gapms.models.dto.technical;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.User;

import java.time.LocalDateTime;

public class ProductionOrderDTO {
    private Long id;
    private BaseEnum status;
    private User createdBy;
    private User approvedBy;
    private PurchaseOrder purchaseOrder;
    private boolean hasWorkOrder; // Để kiểm tra xem ProductionOrder đã có WorkOrder chưa
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public boolean isHasWorkOrder() {
        return hasWorkOrder;
    }

    public void setHasWorkOrder(boolean hasWorkOrder) {
        this.hasWorkOrder = hasWorkOrder;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public BaseEnum getStatus() {
        return status;
    }

    public void setStatus(BaseEnum status) {
        this.status = status;
    }
}