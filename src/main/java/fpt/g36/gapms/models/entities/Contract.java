package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.BaseEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "contract")
public class Contract extends TimestampEntity {
    @Id
    @Column(name = "id", nullable = false, length = 15)
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BaseEnum status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    public Contract() {
    }

    public Contract(LocalDateTime createAt, LocalDateTime updateAt, String id, BaseEnum status, PurchaseOrder purchaseOrder, User approvedBy) {
        super(createAt, updateAt);
        this.id = id;
        this.status = status;
        this.purchaseOrder = purchaseOrder;
        this.approvedBy = approvedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
}