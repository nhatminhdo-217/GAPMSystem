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

    @OneToOne
    @JoinColumn(name = "work_order_detail_id", nullable = false)
    private WorkOrderDetail workOrderDetail;

    @OneToOne
    @JoinColumn(name = "dye_stage_id", nullable = false)
    private DyeStage dyeStage; //Lấy ngày kết thúc nhuộm và khối lượng nhuộm

    @NotNull
    private LocalDateTime receivedConeAt; //Thời gian sợi về

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_start_id")
    private User leaderStart;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_end_id")
    private User leaderEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by")
    private User checkedBy;

    @OneToOne
    @JoinColumn(name = "winding_machine_id", nullable = false)
    private WindingMachine windingMachine;

    @OneToOne(mappedBy = "windingStage")
    private PackagingStage packagingStage;

    @OneToMany(  mappedBy = "windingStage",fetch = FetchType.LAZY)
    private List<WindingRiskAssessment> windingRiskAssessmentList;

    private String windingPhoto;
    public WindingStage() {
    }

    public WindingStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrderDetail workOrderDetail, DyeStage dyeStage, LocalDateTime receivedConeAt, LocalDate deadline, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, TestEnum testStatus, Integer actualOutput, User leaderStart, User leaderEnd, User checkedBy, WindingMachine windingMachine, PackagingStage packagingStage, List<WindingRiskAssessment> windingRiskAssessmentList, String windingPhoto) {
        super(id, createAt, updateAt);
        this.workOrderDetail = workOrderDetail;
        this.dyeStage = dyeStage;
        this.receivedConeAt = receivedConeAt;
        this.deadline = deadline;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.testStatus = testStatus;
        this.actualOutput = actualOutput;
        this.leaderStart = leaderStart;
        this.leaderEnd = leaderEnd;
        this.checkedBy = checkedBy;
        this.windingMachine = windingMachine;
        this.packagingStage = packagingStage;
        this.windingRiskAssessmentList = windingRiskAssessmentList;
        this.windingPhoto = windingPhoto;
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

    public User getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(User checkedBy) {
        this.checkedBy = checkedBy;
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

    public String getWindingPhoto() {
        return windingPhoto;
    }

    public void setWindingPhoto(String windingPhoto) {
        this.windingPhoto = windingPhoto;
    }

    public List<WindingRiskAssessment> getWindingRiskAssessmentList() {
        return windingRiskAssessmentList;
    }

    public void setWindingRiskAssessmentList(List<WindingRiskAssessment> windingRiskAssessmentList) {
        this.windingRiskAssessmentList = windingRiskAssessmentList;
    }
}
