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
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rfq_id", nullable = false)
    private Rfq rfq;

    @OneToOne(mappedBy = "quotation")
    private PurchaseOrder purchaseOrder;

    @OneToMany(mappedBy = "quotation")
    private Set<PurchaseOrderPrice> purchaseOrderPrices = new LinkedHashSet<>();

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationDetail> quotationDetails = new ArrayList<>();

    public Quotation() {
    }

    public Quotation(Long id, LocalDateTime createAt, LocalDateTime updateAt, Boolean isCanceled, BaseEnum isAccepted, Rfq rfq, PurchaseOrder purchaseOrder, Set<PurchaseOrderPrice> purchaseOrderPrices, List<QuotationDetail> quotationDetails) {
        super(id, createAt, updateAt);
        this.isCanceled = isCanceled;
        this.isAccepted = isAccepted;
        this.rfq = rfq;
        this.purchaseOrder = purchaseOrder;
        this.purchaseOrderPrices = purchaseOrderPrices;
        this.quotationDetails = quotationDetails;
    }

    public Boolean getCanceled() {
        return isCanceled;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
    }

    public BaseEnum getAccepted() {
        return isAccepted;
    }

    public void setAccepted(BaseEnum accepted) {
        isAccepted = accepted;
    }

    public Rfq getRfq() {
        return rfq;
    }

    public void setRfq(Rfq rfq) {
        this.rfq = rfq;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Set<PurchaseOrderPrice> getPurchaseOrderPrices() {
        return purchaseOrderPrices;
    }

    public void setPurchaseOrderPrices(Set<PurchaseOrderPrice> purchaseOrderPrices) {
        this.purchaseOrderPrices = purchaseOrderPrices;
    }

    public BaseEnum getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(BaseEnum isAccepted) {
        this.isAccepted = isAccepted;
    }
}