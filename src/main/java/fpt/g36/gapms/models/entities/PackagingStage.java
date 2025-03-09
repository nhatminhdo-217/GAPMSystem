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

    @OneToOne(mappedBy = "packagingStage")
    private WorkOrderDetail workOrderDetail;

    @OneToOne(mappedBy = "packagingStage")
    private WindingStage windingStage; //Lấy ngày kết thúc đánh côn và khối lượng côn

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
}
