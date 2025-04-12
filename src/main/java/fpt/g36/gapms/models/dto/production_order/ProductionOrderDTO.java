package fpt.g36.gapms.models.dto.production_order;

import fpt.g36.gapms.enums.BaseEnum;

import java.time.LocalDateTime;

public class ProductionOrderDTO {
    private Long id;
    private BaseEnum status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Long purchaseOrderId;
    private String approvedBy;
    private String createdBy;

    public ProductionOrderDTO() {
    }

    public ProductionOrderDTO(Long id, BaseEnum status, LocalDateTime createAt, LocalDateTime updateAt, Long purchaseOrderId, String approvedBy, String createdBy) {
        this.id = id;
        this.status = status;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.purchaseOrderId = purchaseOrderId;
        this.approvedBy = approvedBy;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BaseEnum getStatus() {
        return status;
    }

    public void setStatus(BaseEnum status) {
        this.status = status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
