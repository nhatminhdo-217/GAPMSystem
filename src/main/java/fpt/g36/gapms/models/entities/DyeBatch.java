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
public class DyeBatch extends BaseEntity {

    @NotNull
    @Column(name = "liters_min", precision = 10, scale = 2)
    private BigDecimal liters_min;

    @NotNull
    @Column(name = "liters", precision = 10, scale = 2)
    private BigDecimal liters;

    @NotNull
    @Column(name = "cone_batch_weight", precision = 10, scale = 2)
    private BigDecimal cone_batch_weight; // khối lượng chỉ riêng của từng mẻ

    @NotNull
    private LocalDate deadline;

    @NotNull
    private LocalDate plannedStart;

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TestEnum testStatus;

    private String dyePhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_start_id")
    private User leaderStart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_end_id")
    private User leaderEnd;

    @OneToOne(mappedBy = "dyeBatch")
    private WindingBatch windingBatch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dye_stage_id")
    private DyeStage dyeStage;

    @OneToMany(mappedBy = "dyeBatch", fetch = FetchType.LAZY)
    private List<DyeRiskAssessment> dyeRiskAssessments;

    private Boolean isPass;

    public DyeBatch() {
    }

    ;

    public DyeBatch(Long id, LocalDateTime createAt, LocalDateTime updateAt, BigDecimal liters_min, BigDecimal liters, BigDecimal cone_batch_weight, LocalDate deadline, LocalDate plannedStart, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, TestEnum testStatus, String dyePhoto, User leaderStart, User leaderEnd, WindingBatch windingBatch, DyeStage dyeStage, List<DyeRiskAssessment> dyeRiskAssessments, Boolean isPass) {
        super(id, createAt, updateAt);
        this.liters_min = liters_min;
        this.liters = liters;
        this.cone_batch_weight = cone_batch_weight;
        this.deadline = deadline;
        this.plannedStart = plannedStart;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.testStatus = testStatus;
        this.dyePhoto = dyePhoto;
        this.leaderStart = leaderStart;
        this.leaderEnd = leaderEnd;
        this.windingBatch = windingBatch;
        this.dyeStage = dyeStage;
        this.dyeRiskAssessments = dyeRiskAssessments;
        this.isPass = isPass;
    }

    public LocalDate getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDate plannedStart) {
        this.plannedStart = plannedStart;
    }

    public WindingBatch getWindingBatch() {
        return windingBatch;
    }

    public void setWindingBatch(WindingBatch windingBatch) {
        this.windingBatch = windingBatch;
    }

    public BigDecimal getCone_batch_weight() {
        return cone_batch_weight;
    }

    public void setCone_batch_weight(BigDecimal cone_batch_weight) {
        this.cone_batch_weight = cone_batch_weight;
    }

    public DyeStage getDyeStage() {
        return dyeStage;
    }

    public void setDyeStage(DyeStage dyeStage) {
        this.dyeStage = dyeStage;
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

    public Boolean getPass() {
        return isPass;
    }

    public void setPass(Boolean pass) {
        isPass = pass;
    }
}
