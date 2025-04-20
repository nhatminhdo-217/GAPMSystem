package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "winding_stage")
public class WindingStage extends BaseEntity{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_detail_id", nullable = false)
    private WorkOrderDetail workOrderDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dye_stage_id")
    private DyeStage dyeStage; //Lấy ngày kết thúc nhuộm và khối lượng nhuộm

    private LocalDateTime receivedConeAt; //Thời gian sợi về

    @NotNull
    private LocalDateTime plannedStart;

    @NotNull
    private LocalDateTime deadline;

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winding_machine_id")
    private WindingMachine windingMachine;

    @OneToOne(mappedBy = "windingStage", fetch = FetchType.LAZY)
    private PackagingStage packagingStage;

    @OneToMany(mappedBy = "windingStage", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WindingBatch> windingbatches;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "winding_stage_team_leaders",
            joinColumns = @JoinColumn(name = "winding_stage_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> teamLeaders; // Danh sách Team Leaders

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "winding_stage_qa",
            joinColumns = @JoinColumn(name = "winding_stage_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> qa;

    public WindingStage() {
    }

    public WindingStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrderDetail workOrderDetail, DyeStage dyeStage, LocalDateTime receivedConeAt, LocalDateTime plannedStart, LocalDateTime deadline, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, WindingMachine windingMachine, PackagingStage packagingStage, List<WindingBatch> windingbatches, List<User> teamLeaders, List<User> qa) {
        super(id, createAt, updateAt);
        this.workOrderDetail = workOrderDetail;
        this.dyeStage = dyeStage;
        this.receivedConeAt = receivedConeAt;
        this.plannedStart = plannedStart;
        this.deadline = deadline;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.windingMachine = windingMachine;
        this.packagingStage = packagingStage;
        this.windingbatches = windingbatches;
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

    public DyeStage getDyeStage() {
        return dyeStage;
    }

    public void setDyeStage(DyeStage dyeStage) {
        this.dyeStage = dyeStage;
    }

    public LocalDateTime getReceivedConeAt() {
        return receivedConeAt;
    }

    public void setReceivedConeAt(LocalDateTime receivedConeAt) {
        this.receivedConeAt = receivedConeAt;
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

    public WindingMachine getWindingMachine() {
        return windingMachine;
    }

    public void setWindingMachine(WindingMachine windingMachine) {
        this.windingMachine = windingMachine;
    }

    public PackagingStage getPackagingStage() {
        return packagingStage;
    }

    public void setPackagingStage(PackagingStage packagingStage) {
        this.packagingStage = packagingStage;
    }

    public List<WindingBatch> getWindingbatches() {
        return windingbatches;
    }

    public void setWindingbatches(List<WindingBatch> windingbatches) {
        this.windingbatches = windingbatches;
    }
}
