package fpt.g36.gapms.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "winding_process")
public class WindingProcess extends BaseEntity {

    @NotNull
    @Column(name = "machine_speed")
    private Integer machine_speed;

    @NotNull
    @Column(name = "tocdo_banhvot", precision = 10, scale = 1)
    private BigDecimal tocdo_banhvot;

    @NotNull
    @Column(name = "glue_level")
    private Boolean glue_level; //0: nóng, 1: nguội

    public WindingProcess() {
    }

    public WindingProcess(Long id, LocalDateTime createAt, LocalDateTime updateAt, Integer machine_speed, BigDecimal tocdo_banhvot, Boolean glue_level) {
        super(id, createAt, updateAt);
        this.machine_speed = machine_speed;
        this.tocdo_banhvot = tocdo_banhvot;
        this.glue_level = glue_level;
    }

    public Integer getMachine_speed() {
        return machine_speed;
    }

    public void setMachine_speed(Integer machine_speed) {
        this.machine_speed = machine_speed;
    }

    public BigDecimal getTocdo_banhvot() {
        return tocdo_banhvot;
    }

    public void setTocdo_banhvot(BigDecimal tocdo_banhvot) {
        this.tocdo_banhvot = tocdo_banhvot;
    }

    public Boolean getGlue_level() {
        return glue_level;
    }

    public void setGlue_level(Boolean glue_level) {
        this.glue_level = glue_level;
    }
}
