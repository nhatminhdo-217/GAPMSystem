package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_order_detail")
public class WorkOrderDetail extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    @OneToOne(mappedBy = "workOrderDetail")
    private DyeStage dyeStage;

    @OneToOne(mappedBy = "workOrderDetail")
    private WindingStage windingStage;
}