package fpt.g36.gapms.models.dto.technical;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.entities.WorkOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductionOrderDetailsDTO {
    private Long id;
    private BaseEnum status;
    private User createdBy;
    private User approvedBy;
    private PurchaseOrder purchaseOrder;
    private WorkOrder workOrder;
    private List<ProductionOrderDetailItem> productionOrderDetails;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static class ProductionOrderDetailItem {
        private Long id;
        private BigDecimal threadMass; // Khối lượng sợi
        private Boolean lightEnv; // 0: ánh đèn (AD), 1: ánh sáng tự nhiên (TN)
        private String description;
        private boolean hasWorkOrderDetail; // Để kiểm tra xem ProductionOrderDetail đã có WorkOrderDetail chưa

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public BigDecimal getThreadMass() {
            return threadMass;
        }

        public void setThreadMass(BigDecimal threadMass) {
            this.threadMass = threadMass;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getLightEnv() {
            return lightEnv;
        }

        public void setLightEnv(Boolean lightEnv) {
            this.lightEnv = lightEnv;
        }

        public boolean isHasWorkOrderDetail() {
            return hasWorkOrderDetail;
        }

        public void setHasWorkOrderDetail(boolean hasWorkOrderDetail) {
            this.hasWorkOrderDetail = hasWorkOrderDetail;
        }
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

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public List<ProductionOrderDetailItem> getProductionOrderDetails() {
        return productionOrderDetails;
    }

    public void setProductionOrderDetails(List<ProductionOrderDetailItem> productionOrderDetails) {
        this.productionOrderDetails = productionOrderDetails;
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
}
