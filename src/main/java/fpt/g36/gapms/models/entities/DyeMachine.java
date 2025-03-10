package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "dye_machine")
public class DyeMachine extends BaseEntity {

    @OneToOne(mappedBy = "dyeMachine")
    private DyeStage dyeStage;

    @NotNull
    private Integer diameter; // Đường kính

    @NotNull
    private Integer pile; // Số cọc

    @NotNull
    private Integer conePerPile; // Số quả trên mỗi cọc

    @NotNull
    private Integer maxWeight; // Khối lượng tối đa

    @NotNull
    private Integer capacity; // Công suất (quả/h)

    @Lob
    @Column(name = "description")
    private String description;

    public DyeMachine() {
    }

    public DyeMachine(Long id, LocalDateTime createAt, LocalDateTime updateAt, DyeStage dyeStage, Integer diameter, Integer pile, Integer conePerPile, Integer maxWeight, Integer capacity, String description) {
        super(id, createAt, updateAt);
        this.dyeStage = dyeStage;
        this.diameter = diameter;
        this.pile = pile;
        this.conePerPile = conePerPile;
        this.maxWeight = maxWeight;
        this.capacity = capacity;
        this.description = description;
    }

    public DyeStage getDyeStage() {
        return dyeStage;
    }

    public void setDyeStage(DyeStage dyeStage) {
        this.dyeStage = dyeStage;
    }

    public Integer getDiameter() {
        return diameter;
    }

    public void setDiameter(Integer diameter) {
        this.diameter = diameter;
    }

    public Integer getPile() {
        return pile;
    }

    public void setPile(Integer pile) {
        this.pile = pile;
    }

    public Integer getConePerPile() {
        return conePerPile;
    }

    public void setConePerPile(Integer conePerPile) {
        this.conePerPile = conePerPile;
    }

    public Integer getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Integer maxWeight) {
        this.maxWeight = maxWeight;
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
