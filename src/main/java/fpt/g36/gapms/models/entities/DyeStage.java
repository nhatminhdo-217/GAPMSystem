package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dye_stage")
public class DyeStage extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "work_order_detail_id", nullable = false)
    private WorkOrderDetail workOrderDetail;

    @NotNull
    @Column(name = "liters_min", precision = 10, scale = 2)
    private BigDecimal liters_min;

    @NotNull
    @Column(name = "liters", precision = 10, scale = 2)
    private BigDecimal liters;

    @NotNull
    @Column(name = "cone_weight", precision = 10, scale = 2)
    private BigDecimal cone_weight; // Khối lượng

    @NotNull
    @Column(name = "cone_quantity", precision = 10, scale = 2)
    private BigDecimal cone_quantity; // Số quả

    @NotNull
    private LocalDate deadline;

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @NotNull
    private WorkEnum workStatus;

    @NotNull
    private TestEnum testStatus;

    private BigDecimal actualOutput;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by")
    private User checkedBy;

    @OneToOne(mappedBy = "dyeStage")
    private WindingStage windingStage;

    @OneToOne
    @JoinColumn(name = "dye_machine_id")
    private DyeMachine dyeMachine;

    public DyeStage() {
    }

    public DyeStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrderDetail workOrderDetail, BigDecimal liters_min, BigDecimal liters, BigDecimal cone_weight, BigDecimal cone_quantity, LocalDate deadline, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, TestEnum testStatus, BigDecimal actualOutput, User leader, User checkedBy, WindingStage windingStage, DyeMachine dyeMachine) {
        super(id, createAt, updateAt);
        this.workOrderDetail = workOrderDetail;
        this.liters_min = liters_min;
        this.liters = liters;
        this.cone_weight = cone_weight;
        this.cone_quantity = cone_quantity;
        this.deadline = deadline;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.testStatus = testStatus;
        this.actualOutput = actualOutput;
        this.leader = leader;
        this.checkedBy = checkedBy;
        this.windingStage = windingStage;
        this.dyeMachine = dyeMachine;
    }

    public WorkOrderDetail getWorkOrderDetail() {
        return workOrderDetail;
    }

    public void setWorkOrderDetail(WorkOrderDetail workOrderDetail) {
        this.workOrderDetail = workOrderDetail;
    }

    public BigDecimal getLiters_min() {
        return liters_min;
    }

    public void setLiters_min(BigDecimal liters_min) {
        this.liters_min = liters_min;
    }

    public BigDecimal getLiters() {
        return liters;
    }

    public void setLiters(BigDecimal liters) {
        this.liters = liters;
    }

    public BigDecimal getCone_weight() {
        return cone_weight;
    }

    public void setCone_weight(BigDecimal cone_weight) {
        this.cone_weight = cone_weight;
    }

    public BigDecimal getCone_quantity() {
        return cone_quantity;
    }

    public void setCone_quantity(BigDecimal cone_quantity) {
        this.cone_quantity = cone_quantity;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getCompleteAt() {
        return completeAt;
    }

    public void setCompleteAt(LocalDateTime completeAt) {
        this.completeAt = completeAt;
    }

    public WorkEnum getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(WorkEnum workStatus) {
        this.workStatus = workStatus;
    }

    public TestEnum getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(TestEnum testStatus) {
        this.testStatus = testStatus;
    }

    public BigDecimal getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(BigDecimal actualOutput) {
        this.actualOutput = actualOutput;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public User getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(User checkedBy) {
        this.checkedBy = checkedBy;
    }

    public WindingStage getWindingStage() {
        return windingStage;
    }

    public void setWindingStage(WindingStage windingStage) {
        this.windingStage = windingStage;
    }

    public DyeMachine getDyeMachine() {
        return dyeMachine;
    }

    public void setDyeMachine(DyeMachine dyeMachine) {
        this.dyeMachine = dyeMachine;
    }
}
