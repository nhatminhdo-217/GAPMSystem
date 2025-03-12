package fpt.g36.gapms.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "winding_machine")
public class WindingMachine extends BaseEntity {

    @OneToOne(mappedBy = "windingMachine")
    private WindingStage windingStage;

    @NotNull
    private Integer motor_speed;

    @NotNull
    private Integer spindle; // Số trục

    @NotNull
    private Integer capacity; // Công suất (vòng/phút)

    @Lob
    private String description;

    public WindingMachine() {
    }

    public WindingMachine(Long id, LocalDateTime createAt, LocalDateTime updateAt, WindingStage windingStage, Integer motor_speed, Integer spindle, Integer capacity, String description) {
        super(id, createAt, updateAt);
        this.windingStage = windingStage;
        this.motor_speed = motor_speed;
        this.spindle = spindle;
        this.capacity = capacity;
        this.description = description;
    }

    public WindingStage getWindingStage() {
        return windingStage;
    }

    public void setWindingStage(WindingStage windingStage) {
        this.windingStage = windingStage;
    }

    public Integer getMotor_speed() {
        return motor_speed;
    }

    public void setMotor_speed(Integer motor_speed) {
        this.motor_speed = motor_speed;
    }

    public Integer getSpindle() {
        return spindle;
    }

    public void setSpindle(Integer spindle) {
        this.spindle = spindle;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
