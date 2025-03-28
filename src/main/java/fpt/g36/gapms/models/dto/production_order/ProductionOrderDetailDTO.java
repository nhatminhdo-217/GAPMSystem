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
    private Long purchaseOrderDetailId;
    private String brandName;
    private String noteColor;
    private Integer quantity;
    private String category;
    private String threadName;
    private BigDecimal rate;
    private Long process;

    public ProductionOrderDetailDTO() {
    }

    public ProductionOrderDetailDTO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String description, boolean lightEnv, BigDecimal threadMass, Long productionOrderId, Long purchaseOrderDetailId, String brandName, String noteColor, Integer quantity, String category, String threadName, BigDecimal rate, Long process) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
        this.lightEnv = lightEnv;
        this.threadMass = threadMass;
        this.productionOrderId = productionOrderId;
        this.purchaseOrderDetailId = purchaseOrderDetailId;
        this.brandName = brandName;
        this.noteColor = noteColor;
        this.quantity = quantity;
        this.category = category;
        this.threadName = threadName;
        this.rate = rate;
        this.process = process;
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

    public Long getPurchaseOrderDetailId() {
        return purchaseOrderDetailId;
    }

    public void setPurchaseOrderDetailId(Long purchaseOrderDetailId) {
        this.purchaseOrderDetailId = purchaseOrderDetailId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(String noteColor) {
        this.noteColor = noteColor;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Long getProcess() {
        return process;
    }

    public void setProcess(Long process) {
        this.process = process;
    }
}
