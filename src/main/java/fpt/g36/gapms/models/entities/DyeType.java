package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class DyeType extends BaseEntity {
    // tên thuốc nhuộm
    @NotNull
    private String name;

    @NotNull
    private BigDecimal ratio;

    @NotNull
    private BigDecimal lightPercent;

    @NotNull
    private BigDecimal weight;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "technology_process_id")
    private TechnologyProcess technologyProcess;

    public DyeType() {
    }

    ;

    public DyeType(Long id, LocalDateTime createAt, LocalDateTime updateAt, String name, BigDecimal ratio, BigDecimal lightPercent, BigDecimal weight, TechnologyProcess technologyProcess) {
        super(id, createAt, updateAt);
        this.name = name;
        this.ratio = ratio;
        this.lightPercent = lightPercent;
        this.weight = weight;
        this.technologyProcess = technologyProcess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public BigDecimal getLightPercent() {
        return lightPercent;
    }

    public void setLightPercent(BigDecimal lightPercent) {
        this.lightPercent = lightPercent;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public TechnologyProcess getTechnologyProcess() {
        return technologyProcess;
    }

    public void setTechnologyProcess(TechnologyProcess technologyProcess) {
        this.technologyProcess = technologyProcess;
    }
}
