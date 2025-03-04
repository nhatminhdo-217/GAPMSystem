package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.BaseEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Bag;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "quotation")
public class Quotation extends BaseEntity {
    @ColumnDefault("0")
    @Column(name = "is_canceled")
    private Boolean isCanceled;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_accepted")
    private BaseEnum isAccepted;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rfq_id", nullable = false)
    private Rfq rfq;

    @OneToMany(mappedBy = "quotation")
    private Set<PurchaseOrder> purchaseOrders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "quotation")
    private Set<PurchaseOrderPrice> purchaseOrderPrices = new LinkedHashSet<>();

    public Quotation() {
    }

    public Quotation(Long id, LocalDateTime createAt, LocalDateTime updateAt, Boolean isCanceled, Rfq rfq, Set<PurchaseOrder> purchaseOrders, Set<PurchaseOrderPrice> purchaseOrderPrices) {
        super(id, createAt, updateAt);
        this.isCanceled = isCanceled;
        this.rfq = rfq;
        this.purchaseOrders = purchaseOrders;
        this.purchaseOrderPrices = purchaseOrderPrices;
    }

    public Boolean getCanceled() {
        return isCanceled;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
    }

    public Rfq getRfq() {
        return rfq;
    }

    public void setRfq(Rfq rfq) {
        this.rfq = rfq;
    }

    public Set<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(Set<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public Set<PurchaseOrderPrice> getPurchaseOrderPrices() {
        return purchaseOrderPrices;
    }

    public void setPurchaseOrderPrices(Set<PurchaseOrderPrice> purchaseOrderPrices) {
        this.purchaseOrderPrices = purchaseOrderPrices;
    }
}