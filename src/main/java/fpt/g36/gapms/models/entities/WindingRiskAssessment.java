package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class WindingRiskAssessment extends BaseEntity {

    private int falseCone;

    private Boolean isLightTrue;

    private Boolean isColorAdhesion;

    @OneToMany(mappedBy = "windingRiskAssessment")
    private List<PhotoStage> photo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private User createBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "winding_batch_id")
    private WindingBatch windingBatch;

    public WindingRiskAssessment(Long id, LocalDateTime createAt, LocalDateTime updateAt, int falseCone, Boolean isLightTrue, Boolean isColorAdhesion, List<PhotoStage> photo, User createBy, WindingBatch windingBatch) {
        super(id, createAt, updateAt);
        this.falseCone = falseCone;
        this.isLightTrue = isLightTrue;
        this.isColorAdhesion = isColorAdhesion;
        this.photo = photo;
        this.createBy = createBy;
        this.windingBatch = windingBatch;
    }

    public WindingRiskAssessment() {
    }

    public int getFalseCone() {
        return falseCone;
    }

    public void setFalseCone(int falseCone) {
        this.falseCone = falseCone;
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

    public WindingBatch getWindingBatch() {
        return windingBatch;
    }

    public void setWindingBatch(WindingBatch windingBatch) {
        this.windingBatch = windingBatch;
    }
}
