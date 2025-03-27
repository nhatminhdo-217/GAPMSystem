package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TestEnum testStatus;

    @Column(name = "actual_output", precision = 10, scale = 2)
    private BigDecimal actualOutput;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_start_id")
    private User leaderStart;

    @OneToMany(  mappedBy = "dyeStage",fetch = FetchType.LAZY)
    private List<DyeRiskAssessment> dyeRiskAssessments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_end_id")
    private User leaderEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by")
    private User checkedBy;

    @OneToOne(mappedBy = "dyeStage")
    private WindingStage windingStage;

    @OneToOne
    @JoinColumn(name = "dye_machine_id")
    private DyeMachine dyeMachine;

    private String dyePhoto;
    public DyeStage() {
    }

    public DyeStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrderDetail workOrderDetail, BigDecimal liters_min, BigDecimal liters, BigDecimal cone_weight, BigDecimal cone_quantity, LocalDate deadline, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, TestEnum testStatus, BigDecimal actualOutput, User leaderStart, List<DyeRiskAssessment> dyeRiskAssessments, User leaderEnd, User checkedBy, WindingStage windingStage, DyeMachine dyeMachine, String dyePhoto) {
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
        this.leaderStart = leaderStart;
        this.dyeRiskAssessments = dyeRiskAssessments;
        this.leaderEnd = leaderEnd;
        this.checkedBy = checkedBy;
        this.windingStage = windingStage;
        this.dyeMachine = dyeMachine;
        this.dyePhoto = dyePhoto;
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

    public String getDyePhoto() {
        return dyePhoto;
    }

    public void setDyePhoto(String dyePhoto) {
        this.dyePhoto = dyePhoto;
    }

    public User getLeaderStart() {
        return leaderStart;
    }

    public void setLeaderStart(User leaderStart) {
        this.leaderStart = leaderStart;
    }

    public User getLeaderEnd() {
        return leaderEnd;
    }

    public void setLeaderEnd(User leaderEnd) {
        this.leaderEnd = leaderEnd;
    }

    public List<DyeRiskAssessment> getDyeRiskAssessments() {
        return dyeRiskAssessments;
    }

    public void setDyeRiskAssessments(List<DyeRiskAssessment> dyeRiskAssessments) {
        this.dyeRiskAssessments = dyeRiskAssessments;
    }
}
