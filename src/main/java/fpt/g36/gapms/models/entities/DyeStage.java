package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dye_stage")
public class DyeStage extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "work_order_detail_id", nullable = false)
    private WorkOrderDetail workOrderDetail;

    @NotNull
    @Column(name = "liters", precision = 10, scale = 2)
    private BigDecimal liters; // Khối lượng nước tổng = Khối lượng tổng * 6

    @NotNull
    @Column(name = "cone_weight", precision = 10, scale = 2)
    private BigDecimal cone_weight; // Khối lượng tổng

    @NotNull
    @Column(name = "cone_batch_weight", precision = 10, scale = 2)
    private BigDecimal cone_batch_weight; // Khối lượng tối đa của mẻ để tính số quả

    @NotNull
    @Column(name = "cone_quantity", precision = 10, scale = 2)
    private BigDecimal cone_quantity; // Số quả = khối lượng / khối lượng quả(1.25)

    @NotNull
    private LocalDateTime deadline;

    @NotNull
    private LocalDateTime plannedStart;


    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkEnum workStatus;

    @OneToOne(mappedBy = "dyeStage")
    private WindingStage windingStage;

    @OneToOne
    @JoinColumn(name = "dye_machine_id")
    private DyeMachine dyeMachine;

    @OneToMany(mappedBy = "dyeStage", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DyeBatch> dyebatches;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "dye_stage_team_leaders",
            joinColumns = @JoinColumn(name = "dye_stage_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> teamLeaders; // Danh sách Team Leaders

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "dye_stage_qa",
            joinColumns = @JoinColumn(name = "dye_stage_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> qa;

    public DyeStage() {
    }

    public DyeStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, WorkOrderDetail workOrderDetail, BigDecimal liters, BigDecimal cone_weight, BigDecimal cone_batch_weight, BigDecimal cone_quantity, LocalDateTime deadline, LocalDateTime plannedStart, LocalDateTime startAt, LocalDateTime completeAt, WorkEnum workStatus, WindingStage windingStage, DyeMachine dyeMachine, List<DyeBatch> dyebatches, List<User> teamLeaders, List<User> qa) {
        super(id, createAt, updateAt);
        this.workOrderDetail = workOrderDetail;
        this.liters = liters;
        this.cone_weight = cone_weight;
        this.cone_batch_weight = cone_batch_weight;
        this.cone_quantity = cone_quantity;
        this.deadline = deadline;
        this.plannedStart = plannedStart;
        this.startAt = startAt;
        this.completeAt = completeAt;
        this.workStatus = workStatus;
        this.windingStage = windingStage;
        this.dyeMachine = dyeMachine;
        this.dyebatches = dyebatches;
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


    public BigDecimal getCone_batch_weight() {
        return cone_batch_weight;
    }

    public void setCone_batch_weight(BigDecimal cone_batch_weight) {
        this.cone_batch_weight = cone_batch_weight;
    }

    public WorkOrderDetail getWorkOrderDetail() {
        return workOrderDetail;
    }

    public void setWorkOrderDetail(WorkOrderDetail workOrderDetail) {
        this.workOrderDetail = workOrderDetail;
    }

    public BigDecimal getCone_weight() {
        return cone_weight;
    }

    public void setCone_weight(BigDecimal cone_weight) {
        this.cone_weight = cone_weight;
    }

    public BigDecimal getLiters() {
        return liters;
    }

    public void setLiters(BigDecimal liters) {
        this.liters = liters;
    }

    public BigDecimal getCone_quantity() {
        return cone_quantity;
    }

    public void setCone_quantity(BigDecimal cone_quantity) {
        this.cone_quantity = cone_quantity;
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

    public List<DyeBatch> getDyebatches() {
        return dyebatches;
    }

    public void setDyebatches(List<DyeBatch> dyebatches) {
        this.dyebatches = dyebatches;
    }
}
