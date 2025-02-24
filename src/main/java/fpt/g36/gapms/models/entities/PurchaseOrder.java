package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.BaseEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
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

    @OneToMany(mappedBy = "purchaseOrder")
    private Set<Contract> contracts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "purchaseOrder")
    private Set<ProductionOrder> productionOrders = new LinkedHashSet<>();

    public PurchaseOrder() {
    }

    public PurchaseOrder(Long id, LocalDateTime createAt, LocalDateTime updateAt, BaseEnum status, Quotation quotation, Set<Contract> contracts, Set<ProductionOrder> productionOrders) {
        super(id, createAt, updateAt);
        this.status = status;
        this.quotation = quotation;
        this.contracts = contracts;
        this.productionOrders = productionOrders;
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

    public Set<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(Set<Contract> contracts) {
        this.contracts = contracts;
    }

    public Set<ProductionOrder> getProductionOrders() {
        return productionOrders;
    }

    public void setProductionOrders(Set<ProductionOrder> productionOrders) {
        this.productionOrders = productionOrders;
    }


}