package fpt.g36.gapms.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "winding_machine")
public class WindingMachine extends BaseEntity {

    @OneToOne(mappedBy = "windingMachine")
    private WindingStage windingStage;

    private Integer motor_speed;

    private Integer spindle; // Số trục

    private Integer capacity; // Công suất (vòng/phút)
}
