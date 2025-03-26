package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "packaging_stage")
public class PackagingStage extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "work_order_detail_id", nullable = false)
    private WorkOrderDetail workOrderDetail;

    @OneToOne
    @JoinColumn(name = "winding_stage_id", nullable = false)
    private WindingStage windingStage; //Lấy ngày kết thúc đánh côn và khối lượng côn

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

    private Integer actualOutput;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by")
    private User checkedBy;

    private String PackagingPhoto;

    public PackagingStage() {
    }

    public PackagingStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrderDetail workOrderDetail, WindingStage windingStage, LocalDateTime receivedProductAt, LocalDate deadline, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, TestEnum testStatus, Integer actualOutput, User leader, User checkedBy) {
        super(id, createAt, updateAt);
        this.workOrderDetail = workOrderDetail;
        this.windingStage = windingStage;
        this.receivedProductAt = receivedProductAt;
        this.deadline = deadline;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.testStatus = testStatus;
        this.actualOutput = actualOutput;
        this.leader = leader;
        this.checkedBy = checkedBy;
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

    public Integer getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(Integer actualOutput) {
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

    public String getPackagingPhoto() {
        return PackagingPhoto;
    }

    public void setPackagingPhoto(String packagingPhoto) {
        PackagingPhoto = packagingPhoto;
    }
}
