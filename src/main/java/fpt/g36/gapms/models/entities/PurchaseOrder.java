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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer")
    private User customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BaseEnum status;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manage_by")
    private User manageBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution")
    private Solution solution;

    @OneToOne(mappedBy = "purchaseOrder")
    private ProductionOrder productionOrder;

    @OneToMany(mappedBy = "purchaseOrder")
    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();

    public PurchaseOrder() {
    }

    public PurchaseOrder(Long id, LocalDateTime createAt, LocalDateTime updateAt, User customer, BaseEnum status, Quotation quotation, Contract contract, User approvedBy, User manageBy, Solution solution, ProductionOrder productionOrder, List<PurchaseOrderDetail> purchaseOrderDetails) {
        super(id, createAt, updateAt);
        this.customer = customer;
        this.status = status;
        this.quotation = quotation;
        this.contract = contract;
        this.approvedBy = approvedBy;
        this.manageBy = manageBy;
        this.solution = solution;
        this.productionOrder = productionOrder;
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

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public ProductionOrder getProductionOrder() {
        return productionOrder;
    }

    public void setProductionOrder(ProductionOrder productionOrder) {
        this.productionOrder = productionOrder;
    }

    public List<PurchaseOrderDetail> getPurchaseOrderDetails() {
        return purchaseOrderDetails;
    }

    public void setPurchaseOrderDetails(List<PurchaseOrderDetail> purchaseOrderDetails) {
        this.purchaseOrderDetails = purchaseOrderDetails;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getManageBy() {
        return manageBy;
    }

    public void setManageBy(User manageBy) {
        this.manageBy = manageBy;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
}