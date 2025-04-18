package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.SendEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TechnologyProcess extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // chất làm đều màu khi nhuộm
    @NotNull
    private BigDecimal avcoLveDlxPlus;

    // chất càng hoá
    @NotNull
    private BigDecimal chelator;

    // chất giặt
    @NotNull
    private BigDecimal detergent;

    // chất giặt khử
    @NotNull
    private BigDecimal reducingAgent;

    // chất khuếch tán trong nhuộm
    @NotNull
    private BigDecimal dfm;

    // axit giặt
    @NotNull
    private BigDecimal axit;

    // chất đệm axit
    @NotNull
    private BigDecimal anbatex;

    // dung tỷ
    @NotNull
    private BigDecimal liquorRatio;

    private BigDecimal dispergatorN;

    @NotNull
    @OneToMany(mappedBy = "technologyProcess", fetch = FetchType.LAZY)
    private List<DyeType> dyeTypes;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dye_batch_id", nullable = false)
    private DyeBatch dyeBatch;

    @NotNull
    private SendEnum sendStatus;

    public TechnologyProcess() {
    }

    ;

    public TechnologyProcess(Long id, LocalDateTime createAt, LocalDateTime updateAt, User createdBy, BigDecimal avcoLveDlxPlus, BigDecimal chelator, BigDecimal detergent, BigDecimal reducingAgent, BigDecimal dfm, BigDecimal axit, BigDecimal anbatex, BigDecimal liquorRatio, BigDecimal dispergatorN, List<DyeType> dyeTypes, DyeBatch dyeBatch, SendEnum sendStatus) {
        super(id, createAt, updateAt);
        this.createdBy = createdBy;
        this.avcoLveDlxPlus = avcoLveDlxPlus;
        this.chelator = chelator;
        this.detergent = detergent;
        this.reducingAgent = reducingAgent;
        this.dfm = dfm;
        this.axit = axit;
        this.anbatex = anbatex;
        this.liquorRatio = liquorRatio;
        this.dispergatorN = dispergatorN;
        this.dyeTypes = dyeTypes;
        this.dyeBatch = dyeBatch;
        this.sendStatus = sendStatus;
    }

    public SendEnum getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(SendEnum sendStatus) {
        this.sendStatus = sendStatus;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public DyeBatch getDyeBatch() {
        return dyeBatch;
    }

    public void setDyeBatch(DyeBatch dyeBatch) {
        this.dyeBatch = dyeBatch;
    }

    public BigDecimal getAvcoLveDlxPlus() {
        return avcoLveDlxPlus;
    }

    public void setAvcoLveDlxPlus(BigDecimal avcoLveDlxPlus) {
        this.avcoLveDlxPlus = avcoLveDlxPlus;
    }

    public BigDecimal getChelator() {
        return chelator;
    }

    public void setChelator(BigDecimal chelator) {
        this.chelator = chelator;
    }

    public BigDecimal getDetergent() {
        return detergent;
    }

    public void setDetergent(BigDecimal detergent) {
        this.detergent = detergent;
    }

    public BigDecimal getReducingAgent() {
        return reducingAgent;
    }

    public void setReducingAgent(BigDecimal reducingAgent) {
        this.reducingAgent = reducingAgent;
    }

    public BigDecimal getDfm() {
        return dfm;
    }

    public void setDfm(BigDecimal dfm) {
        this.dfm = dfm;
    }

    public BigDecimal getAxit() {
        return axit;
    }

    public void setAxit(BigDecimal axit) {
        this.axit = axit;
    }

    public BigDecimal getAnbatex() {
        return anbatex;
    }

    public void setAnbatex(BigDecimal anbatex) {
        this.anbatex = anbatex;
    }

    public BigDecimal getLiquorRatio() {
        return liquorRatio;
    }

    public void setLiquorRatio(BigDecimal liquorRatio) {
        this.liquorRatio = liquorRatio;
    }

    public BigDecimal getDispergatorN() {
        return dispergatorN;
    }

    public void setDispergatorN(BigDecimal dispergatorN) {
        this.dispergatorN = dispergatorN;
    }

    public List<DyeType> getDyeTypes() {
        return dyeTypes;
    }

    public void setDyeTypes(List<DyeType> dyeTypes) {
        this.dyeTypes = dyeTypes;
    }
}
