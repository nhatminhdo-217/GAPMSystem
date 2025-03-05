package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.BaseEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BaseEnum status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    @OneToOne(mappedBy = "purchaseOrder")
    private Contract contract;

    @OneToMany(mappedBy = "purchaseOrder")
    private Set<ProductionOrder> productionOrders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "purchaseOrder")
    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();

    public PurchaseOrder() {
    }

    public PurchaseOrder(Long id, LocalDateTime createAt, LocalDateTime updateAt, BaseEnum status, Quotation quotation, Contract contract, Set<ProductionOrder> productionOrders, List<PurchaseOrderDetail> purchaseOrderDetails) {
        super(id, createAt, updateAt);
        this.status = status;
        this.quotation = quotation;
        this.contract = contract;
        this.productionOrders = productionOrders;
        this.purchaseOrderDetails = purchaseOrderDetails;
    }

    public BaseEnum getStatus() {
        return status;
    }

    public void setStatus(BaseEnum status) {
        this.status = status;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContracts(Contract contract) {
        this.contract = contract;
    }

    public Set<ProductionOrder> getProductionOrders() {
        return productionOrders;
    }

    public void setProductionOrders(Set<ProductionOrder> productionOrders) {
        this.productionOrders = productionOrders;
    }

    public List<PurchaseOrderDetail> getPurchaseOrderDetails() {
        return purchaseOrderDetails;
    }

    public void setPurchaseOrderDetails(List<PurchaseOrderDetail> purchaseOrderDetails) {
        this.purchaseOrderDetails = purchaseOrderDetails;
    }
}