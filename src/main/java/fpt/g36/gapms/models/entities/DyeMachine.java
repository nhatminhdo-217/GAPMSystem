package fpt.g36.gapms.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dye_machine")
public class DyeMachine extends BaseEntity {

    @OneToOne(mappedBy = "dyeMachine")
    private DyeStage dyeStage;

    private Integer diameter; // Đường kính

    private Integer pile; // Số cọc

    private Integer conePerPile; // Số quả trên mỗi cọc

    private Integer maxWeight; // Khối lượng tối đa

    private Integer capacity; // Công suất (quả/h)
}
