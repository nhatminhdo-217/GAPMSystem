package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "work_order")
public class WorkOrder extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;

    @NotNull
    private LocalDate deadline;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BaseEnum status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SendEnum sendStatus;

    @Enumerated(EnumType.STRING)
    private WorkEnum isProduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @NotNull
    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL)
    private List<WorkOrderDetail> workOrderDetails;

    public WorkOrder() {
    }

    public WorkOrder(Long id, LocalDateTime createAt, LocalDateTime updateAt, ProductionOrder productionOrder, LocalDate deadline, BaseEnum status, SendEnum sendStatus, User approvedBy, User createdBy, List<WorkOrderDetail> workOrderDetails, WorkEnum isProduction) {
        super(id, createAt, updateAt);
        this.productionOrder = productionOrder;
        this.deadline = deadline;
        this.status = status;
        this.sendStatus = sendStatus;
        this.approvedBy = approvedBy;
        this.createdBy = createdBy;
        this.workOrderDetails = workOrderDetails;
        this.isProduction = isProduction;
    }

    public ProductionOrder getProductionOrder() {
        return productionOrder;
    }

    public void setProductionOrder(ProductionOrder productionOrder) {
        this.productionOrder = productionOrder;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public BaseEnum getStatus() {
        return status;
    }

    public void setStatus(BaseEnum status) {
        this.status = status;
    }

    public SendEnum getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(SendEnum sendStatus) {
        this.sendStatus = sendStatus;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<WorkOrderDetail> getWorkOrderDetails() {
        return workOrderDetails;
    }

    public void setWorkOrderDetails(List<WorkOrderDetail> workOrderDetails) {
        this.workOrderDetails = workOrderDetails;
    }

    public WorkEnum getIsProduction() {
        return isProduction;
    }

    public void setIsProduction(WorkEnum isProduction) {
        this.isProduction = isProduction;
    }
}
