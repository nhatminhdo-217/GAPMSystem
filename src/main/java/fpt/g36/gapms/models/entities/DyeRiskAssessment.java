package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class DyeRiskAssessment extends BaseEntity {

    private Boolean isHumidity;

    private Boolean isColorTrue;

    private Boolean isLightTrue;

    private Boolean isColorAdhesion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dye_batch_id")
    private DyeBatch dyeBatch;

    @OneToMany(mappedBy = "dyeRiskAssessment")
    private List<PhotoStage> photo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private User createBy;

    public DyeRiskAssessment(Long id, LocalDateTime createAt, LocalDateTime updateAt, Boolean isHumidity, Boolean isColorTrue, Boolean isLightTrue, Boolean isColorAdhesion, DyeBatch dyeBatch, List<PhotoStage> photo, User createBy) {
        super(id, createAt, updateAt);
        this.isHumidity = isHumidity;
        this.isColorTrue = isColorTrue;
        this.isLightTrue = isLightTrue;
        this.isColorAdhesion = isColorAdhesion;
        this.dyeBatch = dyeBatch;
        this.photo = photo;
        this.createBy = createBy;
    }

    public DyeRiskAssessment() {

    }

    public Boolean getHumidity() {
        return isHumidity;
    }

    public void setHumidity(Boolean humidity) {
        isHumidity = humidity;
    }

    public Boolean getColorTrue() {
        return isColorTrue;
    }

    public void setColorTrue(Boolean colorTrue) {
        isColorTrue = colorTrue;
    }

    public Boolean getLightTrue() {
        return isLightTrue;
    }

    public void setLightTrue(Boolean lightTrue) {
        isLightTrue = lightTrue;
    }

    public Boolean getColorAdhesion() {
        return isColorAdhesion;
    }

    public void setColorAdhesion(Boolean colorAdhesion) {
        isColorAdhesion = colorAdhesion;
    }

    public DyeBatch getDyeBatch() {
        return dyeBatch;
    }

    public void setDyeBatch(DyeBatch dyeBatch) {
        this.dyeBatch = dyeBatch;
    }

    public List<PhotoStage> getPhoto() {
        return photo;
    }

    public void setPhoto(List<PhotoStage> photo) {
        this.photo = photo;
    }

    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }
}
