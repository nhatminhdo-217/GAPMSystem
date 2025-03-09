package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.TestEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "winding_stage")
public class WindingStage extends BaseEntity{

    @OneToOne(mappedBy = "windingStage")
    private WorkOrderDetail workOrderDetail;

    @OneToOne(mappedBy = "windingStage")
    private DyeStage dyeStage; //Lấy ngày kết thúc nhuộm và khối lượng nhuộm

    private LocalDateTime receivedConeAt;

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
    private WindingMachine windingMachine;


}
