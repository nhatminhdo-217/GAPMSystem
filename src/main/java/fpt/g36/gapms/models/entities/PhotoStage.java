package fpt.g36.gapms.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class PhotoStage extends BaseEntity{

    private String photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dye_risk_assessment_id")
    private DyeRiskAssessment dyeRiskAssessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winding_risk_assessment_id")
    private WindingRiskAssessment windingRiskAssessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_risk_assessment_id")
    private PackagingRiskAssessment packagingRiskAssessment;

    public PhotoStage(Long id, LocalDateTime createAt, LocalDateTime updateAt, String photo, DyeRiskAssessment dyeRiskAssessment, WindingRiskAssessment windingRiskAssessment, PackagingRiskAssessment packagingRiskAssessment) {
        super(id, createAt, updateAt);
        this.photo = photo;
        this.dyeRiskAssessment = dyeRiskAssessment;
        this.windingRiskAssessment = windingRiskAssessment;
        this.packagingRiskAssessment = packagingRiskAssessment;
    }

    public PhotoStage() {
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public DyeRiskAssessment getDyeRiskAssessment() {
        return dyeRiskAssessment;
    }

    public void setDyeRiskAssessment(DyeRiskAssessment dyeRiskAssessment) {
        this.dyeRiskAssessment = dyeRiskAssessment;
    }

    public WindingRiskAssessment getWindingRiskAssessment() {
        return windingRiskAssessment;
    }

    public void setWindingRiskAssessment(WindingRiskAssessment windingRiskAssessment) {
        this.windingRiskAssessment = windingRiskAssessment;
    }

    public PackagingRiskAssessment getPackagingRiskAssessment() {
        return packagingRiskAssessment;
    }

    public void setPackagingRiskAssessment(PackagingRiskAssessment packagingRiskAssessment) {
        this.packagingRiskAssessment = packagingRiskAssessment;
    }
}
