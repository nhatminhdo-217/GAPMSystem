package fpt.g36.gapms.models.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class PackagingRiskAssessment extends BaseEntity {

    // tem đầu
    private Boolean firstStamp;

    //tem lõi
    private Boolean coreStamp;

    // tem chuc
    private Boolean dozenStamp;

    // đóng dấu KCS
    private Boolean kcsStamp;

    private Boolean isPass;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "packaging_batch_id")
    private PackagingBatch packagingBatch;

    @OneToMany(mappedBy = "packagingRiskAssessment")
    private List<PhotoStage> photo;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private User createBy;

    @Column(columnDefinition = "TEXT")
    private String errorDetails;

    private Boolean errorLevel;


    @OneToOne(mappedBy = "packagingRiskAssessment")
    private RiskSolution riskSolution;

    public PackagingRiskAssessment() {
    }


    public PackagingRiskAssessment(Long id, LocalDateTime createAt, LocalDateTime updateAt, Boolean firstStamp, Boolean coreStamp, Boolean dozenStamp, Boolean kcsStamp, Boolean isPass, PackagingBatch packagingBatch, List<PhotoStage> photo, User createBy, String errorDetails, Boolean errorLevel, RiskSolution riskSolution) {
        super(id, createAt, updateAt);
        this.firstStamp = firstStamp;
        this.coreStamp = coreStamp;
        this.dozenStamp = dozenStamp;
        this.kcsStamp = kcsStamp;
        this.isPass = isPass;
        this.packagingBatch = packagingBatch;
        this.photo = photo;
        this.createBy = createBy;
        this.errorDetails = errorDetails;
        this.errorLevel = errorLevel;
        this.riskSolution = riskSolution;
    }

    public Boolean getFirstStamp() {
        return firstStamp;
    }

    public void setFirstStamp(Boolean firstStamp) {
        this.firstStamp = firstStamp;
    }

    public Boolean getCoreStamp() {
        return coreStamp;
    }

    public void setCoreStamp(Boolean coreStamp) {
        this.coreStamp = coreStamp;
    }

    public Boolean getDozenStamp() {
        return dozenStamp;
    }

    public void setDozenStamp(Boolean dozenStamp) {
        this.dozenStamp = dozenStamp;
    }

    public Boolean getKcsStamp() {
        return kcsStamp;
    }

    public void setKcsStamp(Boolean kcsStamp) {
        this.kcsStamp = kcsStamp;
    }

    public Boolean getPass() {
        return isPass;
    }

    public void setPass(Boolean pass) {
        isPass = pass;
    }

    public PackagingBatch getPackagingBatch() {
        return packagingBatch;
    }

    public void setPackagingBatch(PackagingBatch packagingBatch) {
        this.packagingBatch = packagingBatch;
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

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public Boolean getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(Boolean errorLevel) {
        this.errorLevel = errorLevel;
    }

    public RiskSolution getRiskSolution() {
        return riskSolution;
    }

    public void setRiskSolution(RiskSolution riskSolution) {
        this.riskSolution = riskSolution;
    }
}
