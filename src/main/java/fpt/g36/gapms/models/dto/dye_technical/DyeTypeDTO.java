package fpt.g36.gapms.models.dto.dye_technical;

import java.math.BigDecimal;

public class DyeTypeDTO {
    private String name;
    private BigDecimal ratio;
    private BigDecimal lightPercent;
    private BigDecimal weight;

    // Getters and Setters
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
}