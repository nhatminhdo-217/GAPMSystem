package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "winding_batch")
public class WindingBatch extends BaseEntity {

    private int batchNumber;

    private int plannedOutput;

    @NotNull
    private LocalDateTime plannedStart;

    @NotNull
    private LocalDateTime deadline;

    private LocalDateTime receivedConeAt; //Thời gian sợi về

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TestEnum testStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_start_id")
    private User leaderStart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_end_id")
    private User leaderEnd;

    private String windingPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winding_stage_id")
    private WindingStage windingStage;

    private Boolean isPass;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dye_batch_id")
    private DyeBatch dyeBatch;

    @OneToMany(mappedBy = "windingBatch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WindingRiskAssessment> windingRiskAssessmentList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_id")
    private User qa;

    @OneToOne(mappedBy = "windingBatch")
    private PackagingBatch packagingBatch;
    public WindingBatch() {
    }

    public WindingBatch(Long id, LocalDateTime createAt, LocalDateTime updateAt, int batchNumber, int plannedOutput, LocalDateTime plannedStart, LocalDateTime deadline, LocalDateTime receivedConeAt, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, TestEnum testStatus, User leaderStart, User leaderEnd, String windingPhoto, WindingStage windingStage, Boolean isPass, DyeBatch dyeBatch, List<WindingRiskAssessment> windingRiskAssessmentList, User qa, PackagingBatch packagingBatch) {
        super(id, createAt, updateAt);
        this.batchNumber = batchNumber;
        this.plannedOutput = plannedOutput;
        this.plannedStart = plannedStart;
        this.deadline = deadline;
        this.receivedConeAt = receivedConeAt;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.testStatus = testStatus;
        this.leaderStart = leaderStart;
        this.leaderEnd = leaderEnd;
        this.windingPhoto = windingPhoto;
        this.windingStage = windingStage;
        this.isPass = isPass;
        this.dyeBatch = dyeBatch;
        this.windingRiskAssessmentList = windingRiskAssessmentList;
        this.qa = qa;
        this.packagingBatch = packagingBatch;
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

    public LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getReceivedConeAt() {
        return receivedConeAt;
    }

    public void setReceivedConeAt(LocalDateTime receivedConeAt) {
        this.receivedConeAt = receivedConeAt;
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

    public String getWindingPhoto() {
        return windingPhoto;
    }

    public void setWindingPhoto(String windingPhoto) {
        this.windingPhoto = windingPhoto;
    }

    public WindingStage getWindingStage() {
        return windingStage;
    }

    public void setWindingStage(WindingStage windingStage) {
        this.windingStage = windingStage;
    }

    public Boolean getPass() {
        return isPass;
    }

    public void setPass(Boolean pass) {
        isPass = pass;
    }

    public DyeBatch getDyeBatch() {
        return dyeBatch;
    }

    public void setDyeBatch(DyeBatch dyeBatch) {
        this.dyeBatch = dyeBatch;
    }

    public List<WindingRiskAssessment> getWindingRiskAssessmentList() {
        return windingRiskAssessmentList;
    }

    public void setWindingRiskAssessmentList(List<WindingRiskAssessment> windingRiskAssessmentList) {
        this.windingRiskAssessmentList = windingRiskAssessmentList;
    }

    public User getQa() {
        return qa;
    }

    public void setQa(User qa) {
        this.qa = qa;
    }

    public PackagingBatch getPackagingBatch() {
        return packagingBatch;
    }

    public void setPackagingBatch(PackagingBatch packagingBatch) {
        this.packagingBatch = packagingBatch;
    }
}
