package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.WorkEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class RiskSolution extends BaseEntity{

    private BigDecimal weightNeed;

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToOne
    @JoinColumn(name = "dye_risk_assessment_id")
    private DyeRiskAssessment dyeRiskAssessment;

    @OneToOne
    @JoinColumn(name = "winding_risk_assessment_id")
    private WindingRiskAssessment windingRiskAssessment;

    @OneToOne
    @JoinColumn(name = "packaging_risk_assessment_id")
    private PackagingRiskAssessment packagingRiskAssessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BaseEnum approveStatus;

    public RiskSolution(Long id, LocalDateTime createAt, LocalDateTime updateAt, BigDecimal weightNeed, String note, DyeRiskAssessment dyeRiskAssessment, WindingRiskAssessment windingRiskAssessment, PackagingRiskAssessment packagingRiskAssessment, User createdBy, User approvedBy, BaseEnum approveStatus) {
        super(id, createAt, updateAt);
        this.weightNeed = weightNeed;
        this.note = note;
        this.dyeRiskAssessment = dyeRiskAssessment;
        this.windingRiskAssessment = windingRiskAssessment;
        this.packagingRiskAssessment = packagingRiskAssessment;
        this.createdBy = createdBy;
        this.approvedBy = approvedBy;
        this.approveStatus = approveStatus;
    }

    public RiskSolution() {
    }


    public BigDecimal getWeightNeed() {
        return weightNeed;
    }

    public void setWeightNeed(BigDecimal weightNeed) {
        this.weightNeed = weightNeed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public BaseEnum getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(BaseEnum approveStatus) {
        this.approveStatus = approveStatus;
    }
}
