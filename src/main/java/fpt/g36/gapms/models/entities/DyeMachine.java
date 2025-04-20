package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dye_machine")
public class DyeMachine extends BaseEntity {

    @OneToOne(mappedBy = "dyeMachine")
    private DyeStage dyeStage;

    @NotNull
    private BigDecimal diameter; // Đường kính

    @NotNull
    private BigDecimal pile; // Số cọc

    @NotNull
    private BigDecimal conePerPile; // Số quả trên mỗi cọc

    @NotNull
    private BigDecimal maxWeight; // Khối lượng tối đa

    @NotNull
    private BigDecimal littersMin;

    @NotNull
    private BigDecimal littersMax;

    @NotNull
    private BigDecimal coneMin;

    @NotNull
    private BigDecimal coneMax;

    @NotNull
    private BigDecimal capacity; // Công suất (quả/h)

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean isActive = true;

    public DyeMachine() {
    }

    public DyeMachine(Long id, LocalDateTime createAt, LocalDateTime updateAt, DyeStage dyeStage, BigDecimal diameter, BigDecimal pile, BigDecimal conePerPile, BigDecimal maxWeight, BigDecimal littersMin, BigDecimal littersMax, BigDecimal coneMin, BigDecimal coneMax, BigDecimal capacity, String description, boolean isActive) {
        super(id, createAt, updateAt);
        this.dyeStage = dyeStage;
        this.diameter = diameter;
        this.pile = pile;
        this.conePerPile = conePerPile;
        this.maxWeight = maxWeight;
        this.littersMin = littersMin;
        this.littersMax = littersMax;
        this.coneMin = coneMin;
        this.coneMax = coneMax;
        this.capacity = capacity;
        this.description = description;
        this.isActive = isActive;
    }

    public DyeStage getDyeStage() {
        return dyeStage;
    }

    public void setDyeStage(DyeStage dyeStage) {
        this.dyeStage = dyeStage;
    }

    public BigDecimal getDiameter() {
        return diameter;
    }

    public void setDiameter(BigDecimal diameter) {
        this.diameter = diameter;
    }

    public BigDecimal getPile() {
        return pile;
    }

    public void setPile(BigDecimal pile) {
        this.pile = pile;
    }

    public BigDecimal getConePerPile() {
        return conePerPile;
    }

    public void setConePerPile(BigDecimal conePerPile) {
        this.conePerPile = conePerPile;
    }

    public BigDecimal getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(BigDecimal maxWeight) {
        this.maxWeight = maxWeight;
    }

    public BigDecimal getLittersMin() {
        return littersMin;
    }

    public void setLittersMin(BigDecimal littersMin) {
        this.littersMin = littersMin;
    }

    public BigDecimal getLittersMax() {
        return littersMax;
    }

    public void setLittersMax(BigDecimal littersMax) {
        this.littersMax = littersMax;
    }

    public BigDecimal getConeMin() {
        return coneMin;
    }

    public void setConeMin(BigDecimal coneMin) {
        this.coneMin = coneMin;
    }

    public BigDecimal getConeMax() {
        return coneMax;
    }

    public void setConeMax(BigDecimal coneMax) {
        this.coneMax = coneMax;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
