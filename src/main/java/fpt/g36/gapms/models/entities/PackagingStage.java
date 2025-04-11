package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "packaging_stage")
public class PackagingStage extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "work_order_detail_id", nullable = false)
    private WorkOrderDetail workOrderDetail;

    @OneToOne
    @JoinColumn(name = "winding_stage_id", nullable = false)
    private WindingStage windingStage;

    private LocalDateTime receivedProductAt;

    @NotNull
    private LocalDateTime deadline;

    @NotNull
    private LocalDateTime plannedStart;


    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @OneToMany(mappedBy = "packagingStage", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackagingBatch> packagingBatches;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "packaging_stage_team_leaders",
            joinColumns = @JoinColumn(name = "packaging_stage_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> teamLeaders; // Danh sách Team Leaders

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "packaging_stage_qa",
            joinColumns = @JoinColumn(name = "packaging_stage_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> qa;

    public PackagingStage() {
    }

    public PackagingStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrderDetail workOrderDetail, WindingStage windingStage, LocalDateTime receivedProductAt, LocalDateTime deadline, LocalDateTime plannedStart, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, List<PackagingBatch> packagingBatches, List<User> teamLeaders, List<User> qa) {
        super(id, createAt, updateAt);
        this.workOrderDetail = workOrderDetail;
        this.windingStage = windingStage;
        this.receivedProductAt = receivedProductAt;
        this.deadline = deadline;
        this.plannedStart = plannedStart;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.packagingBatches = packagingBatches;
        this.teamLeaders = teamLeaders;
        this.qa = qa;
    }

    public List<User> getTeamLeaders() {
        return teamLeaders;
    }

    public void setTeamLeaders(List<User> teamLeaders) {
        this.teamLeaders = teamLeaders;
    }

    public List<User> getQa() {
        return qa;
    }

    public void setQa(List<User> qa) {
        this.qa = qa;
    }

    public LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
    }


    public WorkOrderDetail getWorkOrderDetail() {
        return workOrderDetail;
    }

    public void setWorkOrderDetail(WorkOrderDetail workOrderDetail) {
        this.workOrderDetail = workOrderDetail;
    }

    public WindingStage getWindingStage() {
        return windingStage;
    }

    public void setWindingStage(WindingStage windingStage) {
        this.windingStage = windingStage;
    }

    public LocalDateTime getReceivedProductAt() {
        return receivedProductAt;
    }

    public void setReceivedProductAt(LocalDateTime receivedProductAt) {
        this.receivedProductAt = receivedProductAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
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

    public List<PackagingBatch> getPackagingBatches() {
        return packagingBatches;
    }

    public void setPackagingBatches(List<PackagingBatch> packagingBatches) {
        this.packagingBatches = packagingBatches;
    }
}
