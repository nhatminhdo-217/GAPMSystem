package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "production_order_detail")
public class ProductionOrderDetail extends BaseEntity {

    @Column(name = "thread_mass", precision = 10, scale = 4)
    private BigDecimal thread_mass; // Khối lượng sợi

    private Boolean light_env; // 0: ánh đèn (AD), 1: ánh sáng tự nhiên (TN)

    @Lob
    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;

    @OneToOne(mappedBy = "productionOrderDetail")
    private WorkOrderDetail workOrderDetail;

    public ProductionOrderDetail() {
    }

    public ProductionOrderDetail(Long id, LocalDateTime createAt, LocalDateTime updateAt, BigDecimal thread_mass, Boolean light_env, String description, ProductionOrder productionOrder, WorkOrderDetail workOrderDetail) {
        super(id, createAt, updateAt);
        this.thread_mass = thread_mass;
        this.light_env = light_env;
        this.description = description;
        this.productionOrder = productionOrder;
        this.workOrderDetail = workOrderDetail;
    }

    public BigDecimal getThread_mass() {
        return thread_mass;
    }

    public void setThread_mass(BigDecimal thread_mass) {
        this.thread_mass = thread_mass;
    }

    public Boolean getLight_env() {
        return light_env;
    }

    public void setLight_env(Boolean light_env) {
        this.light_env = light_env;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductionOrder getProductionOrder() {
        return productionOrder;
    }

    public void setProductionOrder(ProductionOrder productionOrder) {
        this.productionOrder = productionOrder;
    }

    public WorkOrderDetail getWorkOrderDetail() {
        return workOrderDetail;
    }

    public void setWorkOrderDetail(WorkOrderDetail workOrderDetail) {
        this.workOrderDetail = workOrderDetail;
    }
}
