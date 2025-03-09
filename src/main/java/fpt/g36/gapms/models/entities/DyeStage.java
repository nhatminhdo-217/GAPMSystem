package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dye_stage")
public class DyeStage extends BaseEntity{

    @OneToOne(mappedBy = "dyeStage")
    private WorkOrderDetail workOrderDetail;

    @Column(name = "liters_min", precision = 10, scale = 2)
    private BigDecimal liters_min;

    @Column(name = "liters", precision = 10, scale = 2)
    private BigDecimal liters;

    @Column(name = "cone_weight", precision = 10, scale = 2)
    private BigDecimal cone_weight; // Khối lượng

    @Column(name = "cone_quantity", precision = 10, scale = 2)
    private BigDecimal cone_quantity; // Số quả

    private LocalDate deadline;

    private LocalDateTime startAt;

    private LocalDateTime completeAt;

    private WorkEnum workStatus;

    private TestEnum testStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by")
    private User checkedBy;

    @OneToOne(mappedBy = "dyeStage")
    private WindingStage windingStage;

    @OneToOne(mappedBy = "dyeStage")
    private DyeMachine dyeMachine;
}
