package fpt.g36.gapms.models.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class PackagingRiskAssessment extends BaseEntity{

    private Boolean label;

    private Boolean nylon;

    private Boolean isPackaged;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "packaging_stage_id")
    private PackagingStage packagingStage;

    @OneToMany(mappedBy = "packagingRiskAssessment")
    private List<PhotoStage> photo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private User createBy;

    public PackagingRiskAssessment(Long id, LocalDateTime createAt, LocalDateTime updateAt, Boolean label, Boolean nylon, Boolean isPackaged, PackagingStage packagingStage, List<PhotoStage> photo, User createBy) {
        super(id, createAt, updateAt);
        this.label = label;
        this.nylon = nylon;
        this.isPackaged = isPackaged;
        this.packagingStage = packagingStage;
        this.photo = photo;
        this.createBy = createBy;
    }

    public PackagingRiskAssessment() {
    }

    public Boolean getLabel() {
        return label;
    }

    public void setLabel(Boolean label) {
        this.label = label;
    }

    public Boolean getNylon() {
        return nylon;
    }

    public void setNylon(Boolean nylon) {
        this.nylon = nylon;
    }

    public Boolean getPackaged() {
        return isPackaged;
    }

    public void setPackaged(Boolean packaged) {
        isPackaged = packaged;
    }

    public PackagingStage getPackagingStage() {
        return packagingStage;
    }

    public void setPackagingStage(PackagingStage packagingStage) {
        this.packagingStage = packagingStage;
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
