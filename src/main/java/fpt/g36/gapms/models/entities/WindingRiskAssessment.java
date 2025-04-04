package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class WindingRiskAssessment extends BaseEntity {
    //số cuộn chỉ hoàn thành
    private Integer trueCone;

    //số cuộn chỉ lỗi
    private Integer falseCone;

    //độ đồng đều ánh màu
    private Boolean isColorUniformity;

    //loang màu sau côn
    private Boolean isColorFading;

    @OneToMany(mappedBy = "windingRiskAssessment")
    private List<PhotoStage> photo;

    private Boolean isPass;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private User createBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "winding_batch_id")
    private WindingBatch windingBatch;

    public WindingRiskAssessment(Long id, LocalDateTime createAt, LocalDateTime updateAt, Integer trueCone, Integer falseCone, Boolean isColorUniformity, Boolean isColorFading, List<PhotoStage> photo, Boolean isPass, User createBy, WindingBatch windingBatch) {
        super(id, createAt, updateAt);
        this.trueCone = trueCone;
        this.falseCone = falseCone;
        this.isColorUniformity = isColorUniformity;
        this.isColorFading = isColorFading;
        this.photo = photo;
        this.isPass = isPass;
        this.createBy = createBy;
        this.windingBatch = windingBatch;
    }

    public WindingRiskAssessment() {
    }

    public Integer getFalseCone() {
        return falseCone;
    }

    public void setFalseCone(Integer falseCone) {
        this.falseCone = falseCone;
    }

    public Boolean getColorUniformity() {
        return isColorUniformity;
    }

    public void setColorUniformity(Boolean colorUniformity) {
        isColorUniformity = colorUniformity;
    }

    public Boolean getColorFading() {
        return isColorFading;
    }

    public void setColorFading(Boolean colorFading) {
        isColorFading = colorFading;
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

    public Integer getTrueCone() {
        return trueCone;
    }

    public void setTrueCone(Integer trueCone) {
        this.trueCone = trueCone;
    }

    public Boolean getPass() {
        return isPass;
    }

    public void setPass(Boolean pass) {
        isPass = pass;
    }
}
