package fpt.g36.gapms.models.dto.production_order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductionOrderDetailDTO {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
    private boolean lightEnv;
    private BigDecimal threadMass;
    private Long productionOrderId;

    public ProductionOrderDetailDTO() {
    }

    public ProductionOrderDetailDTO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String description, boolean lightEnv, BigDecimal threadMass, Long productionOrderId) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
        this.lightEnv = lightEnv;
        this.threadMass = threadMass;
        this.productionOrderId = productionOrderId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isLightEnv() {
        return lightEnv;
    }

    public void setLightEnv(boolean lightEnv) {
        this.lightEnv = lightEnv;
    }

    public BigDecimal getThreadMass() {
        return threadMass;
    }

    public void setThreadMass(BigDecimal threadMass) {
        this.threadMass = threadMass;
    }

    public Long getProductionOrderId() {
        return productionOrderId;
    }

    public void setProductionOrderId(Long productionOrderId) {
        this.productionOrderId = productionOrderId;
    }
}
