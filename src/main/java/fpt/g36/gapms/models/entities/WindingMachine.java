package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "winding_machine")
public class WindingMachine extends BaseEntity {

    @OneToOne(mappedBy = "windingMachine")
    private WindingStage windingStage;

    @NotNull
    private BigDecimal motor_speed;

    @NotNull
    private BigDecimal spindle; // Số trục

    @NotNull
    private BigDecimal capacity; // Công suất (vòng/phút)

    @Lob
    private String description;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean isActive = true;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public WindingMachine() {
    }

    public WindingMachine(Long id, LocalDateTime createAt, LocalDateTime updateAt, WindingStage windingStage, BigDecimal motor_speed, BigDecimal spindle, BigDecimal capacity, String description, boolean isActive) {
        super(id, createAt, updateAt);
        this.windingStage = windingStage;
        this.motor_speed = motor_speed;
        this.spindle = spindle;
        this.capacity = capacity;
        this.description = description;
        this.isActive = isActive;
    }

    public WindingStage getWindingStage() {
        return windingStage;
    }

    public void setWindingStage(WindingStage windingStage) {
        this.windingStage = windingStage;
    }

    public BigDecimal getMotor_speed() {
        return motor_speed;
    }

    public void setMotor_speed(BigDecimal motor_speed) {
        this.motor_speed = motor_speed;
    }

    public BigDecimal getSpindle() {
        return spindle;
    }

    public void setSpindle(BigDecimal spindle) {
        this.spindle = spindle;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
