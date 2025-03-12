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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rfq_id", nullable = false)
    private Rfq rfq;

    @OneToOne(mappedBy = "quotation")
    private PurchaseOrder purchaseOrder;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationDetail> quotationDetails = new ArrayList<>();

    public Quotation() {
    }

    public Quotation(Long id, LocalDateTime createAt, LocalDateTime updateAt, Boolean isCanceled, BaseEnum isAccepted, User createdBy, Rfq rfq, PurchaseOrder purchaseOrder, List<QuotationDetail> quotationDetails) {
        super(id, createAt, updateAt);
        this.isCanceled = isCanceled;
        this.isAccepted = isAccepted;
        this.createdBy = createdBy;
        this.rfq = rfq;
        this.purchaseOrder = purchaseOrder;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
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

    public BaseEnum getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(BaseEnum isAccepted) {
        this.isAccepted = isAccepted;
    }
}