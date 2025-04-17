package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class DyeRiskAssessment extends BaseEntity{

      // độ ẩm
      private Boolean isHumidity;

      //màu đúng
      private Boolean isColorTrue;

      // ánh sáng
      private Boolean isLightTrue;


      // loang màu
      private Boolean isColorFading;

      // bám thuốc
      private Boolean isMedication;

      // két thuốc
      private Boolean isMedicineSafe;

      //bẩn vệ sinh công nghiệp
      private Boolean isIndustrialCleaningStains;

      private Boolean isPass;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dye_batch_id")
    private DyeBatch dyeBatch;

    @OneToMany(mappedBy = "dyeRiskAssessment", fetch = FetchType.LAZY)
    private List<PhotoStage> photo;


      @ManyToOne(fetch = FetchType.LAZY, optional = false)
      @JoinColumn(name = "create_by")
      private User createBy;

     @Column(columnDefinition = "TEXT")
      private String errorDetails;

      private Boolean errorLevel;

    @OneToOne(mappedBy = "dyeRiskAssessment", fetch = FetchType.LAZY)
    private RiskSolution riskSolution;


    public DyeRiskAssessment(Long id, LocalDateTime createAt, LocalDateTime updateAt, Boolean isHumidity, Boolean isColorTrue, Boolean isLightTrue, Boolean isColorFading, Boolean isMedication, Boolean isMedicineSafe, Boolean isIndustrialCleaningStains, Boolean isPass, DyeBatch dyeBatch, List<PhotoStage> photo, User createBy, String errorDetails, Boolean errorLevel, RiskSolution riskSolution) {
        super(id, createAt, updateAt);
        this.isHumidity = isHumidity;
        this.isColorTrue = isColorTrue;
        this.isLightTrue = isLightTrue;
        this.isColorFading = isColorFading;
        this.isMedication = isMedication;
        this.isMedicineSafe = isMedicineSafe;
        this.isIndustrialCleaningStains = isIndustrialCleaningStains;
        this.isPass = isPass;
        this.dyeBatch = dyeBatch;
        this.photo = photo;
        this.createBy = createBy;
        this.errorDetails = errorDetails;
        this.errorLevel = errorLevel;
        this.riskSolution = riskSolution;
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

      public Boolean getColorFading() {
            return isColorFading;
      }

      public void setColorFading(Boolean colorFading) {
            isColorFading = colorFading;
      }

      public Boolean getMedication() {
            return isMedication;
      }

      public void setMedication(Boolean medication) {
            isMedication = medication;
      }

      public Boolean getMedicineSafe() {
            return isMedicineSafe;
      }

      public void setMedicineSafe(Boolean medicineSafe) {
            isMedicineSafe = medicineSafe;
      }

      public Boolean getIndustrialCleaningStains() {
            return isIndustrialCleaningStains;
      }

      public void setIndustrialCleaningStains(Boolean industrialCleaningStains) {
            isIndustrialCleaningStains = industrialCleaningStains;
      }

      public Boolean getPass() {
            return isPass;
      }

      public void setPass(Boolean pass) {
            isPass = pass;
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
