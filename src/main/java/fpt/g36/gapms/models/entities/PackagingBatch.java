package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "packaging_batch")
public class PackagingBatch extends BaseEntity {

    @NotNull
    private LocalDate plannedStart;

    @NotNull
    private LocalDateTime receivedProductAt;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_start_id")
    private User leaderStart;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_end_id")
    private User leaderEnd;

    private String PackagingPhoto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "packaging_stage_id")
    private PackagingStage packagingStage;

    @OneToOne
    @JoinColumn(name = "winding_batch_id", nullable = false)
    private WindingBatch windingBatch;

    @OneToMany(mappedBy = "packagingBatch", fetch = FetchType.LAZY)
    private List<PackagingRiskAssessment> packagingRiskAssessments;

    public PackagingBatch() {
    }

    ;

    public PackagingBatch(Long id, LocalDateTime createAt, LocalDateTime updateAt, LocalDate plannedStart, LocalDateTime receivedProductAt, LocalDate deadline, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, TestEnum testStatus, User leaderStart, User leaderEnd, String packagingPhoto, PackagingStage packagingStage, WindingBatch windingBatch, List<PackagingRiskAssessment> packagingRiskAssessments) {
        super(id, createAt, updateAt);
        this.plannedStart = plannedStart;
        this.receivedProductAt = receivedProductAt;
        this.deadline = deadline;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.testStatus = testStatus;
        this.leaderStart = leaderStart;
        this.leaderEnd = leaderEnd;
        PackagingPhoto = packagingPhoto;
        this.packagingStage = packagingStage;
        this.windingBatch = windingBatch;
        this.packagingRiskAssessments = packagingRiskAssessments;
    }

    public LocalDate getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDate plannedStart) {
        this.plannedStart = plannedStart;
    }

    public LocalDateTime getReceivedProductAt() {
        return receivedProductAt;
    }

    public void setReceivedProductAt(LocalDateTime receivedProductAt) {
        this.receivedProductAt = receivedProductAt;
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

    public String getPackagingPhoto() {
        return PackagingPhoto;
    }

    public void setPackagingPhoto(String packagingPhoto) {
        PackagingPhoto = packagingPhoto;
    }

    public PackagingStage getPackagingStage() {
        return packagingStage;
    }

    public void setPackagingStage(PackagingStage packagingStage) {
        this.packagingStage = packagingStage;
    }

    public WindingBatch getWindingBatch() {
        return windingBatch;
    }

    public void setWindingBatch(WindingBatch windingBatch) {
        this.windingBatch = windingBatch;
    }

    public List<PackagingRiskAssessment> getPackagingRiskAssessments() {
        return packagingRiskAssessments;
    }

    public void setPackagingRiskAssessments(List<PackagingRiskAssessment> packagingRiskAssessments) {
        this.packagingRiskAssessments = packagingRiskAssessments;
    }
}
