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

    private int batchNumber;

    private int plannedOutput;

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
    private LocalDateTime deadline;

    @NotNull
    private LocalDateTime plannedStart;


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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_id")
    private User qa;

    @OneToOne(mappedBy = "dyeBatch", fetch = FetchType.LAZY)
    private WindingBatch windingBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dye_stage_id")
    private DyeStage dyeStage;

    @OneToMany(mappedBy = "dyeBatch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DyeRiskAssessment> dyeRiskAssessments;

    @OneToOne(mappedBy = "dyeBatch", fetch = FetchType.LAZY)
    private TechnologyProcess technologyProcess;

    private Boolean isPass;

    public DyeBatch() {
    }

    public DyeBatch(Long id, LocalDateTime createAt, LocalDateTime updateAt, int batchNumber, int plannedOutput, BigDecimal liters_min, BigDecimal liters, BigDecimal cone_batch_weight, LocalDateTime deadline, LocalDateTime plannedStart, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, TestEnum testStatus, String dyePhoto, User leaderStart, User leaderEnd, User qa, WindingBatch windingBatch, DyeStage dyeStage, List<DyeRiskAssessment> dyeRiskAssessments, TechnologyProcess technologyProcess, Boolean isPass) {
        super(id, createAt, updateAt);
        this.batchNumber = batchNumber;
        this.plannedOutput = plannedOutput;
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
        this.qa = qa;
        this.windingBatch = windingBatch;
        this.dyeStage = dyeStage;
        this.dyeRiskAssessments = dyeRiskAssessments;
        this.technologyProcess = technologyProcess;
        this.isPass = isPass;
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
    }

    public int getPlannedOutput() {
        return plannedOutput;
    }

    public void setPlannedOutput(int plannedOutput) {
        this.plannedOutput = plannedOutput;
    }

    public TechnologyProcess getTechnologyProcess() {
        return technologyProcess;
    }

    public void setTechnologyProcess(TechnologyProcess technologyProcess) {
        this.technologyProcess = technologyProcess;
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

    public BigDecimal getCone_batch_weight() {
        return cone_batch_weight;
    }

    public void setCone_batch_weight(BigDecimal cone_batch_weight) {
        this.cone_batch_weight = cone_batch_weight;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
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

    public User getQa() {
        return qa;
    }

    public void setQa(User qa) {
        this.qa = qa;
    }

    public WindingBatch getWindingBatch() {
        return windingBatch;
    }

    public void setWindingBatch(WindingBatch windingBatch) {
        this.windingBatch = windingBatch;
    }

    public DyeStage getDyeStage() {
        return dyeStage;
    }

    public void setDyeStage(DyeStage dyeStage) {
        this.dyeStage = dyeStage;
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
