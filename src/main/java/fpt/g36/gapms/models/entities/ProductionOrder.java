package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.BaseEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "production_order")
public class ProductionOrder extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BaseEnum status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    public ProductionOrder() {
    }

    public ProductionOrder(Long id, LocalDateTime createAt, LocalDateTime updateAt, BaseEnum status, PurchaseOrder purchaseOrder) {
        super(id, createAt, updateAt);
        this.status = status;
        this.purchaseOrder = purchaseOrder;
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
}