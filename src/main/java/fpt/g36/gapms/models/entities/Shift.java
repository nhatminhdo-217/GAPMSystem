package fpt.g36.gapms.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Shift extends BaseEntity{


    private String shiftName;

    private LocalTime shiftStart;

    private LocalTime shiftEnd;

    private String Description;

    @OneToOne(mappedBy = "shift")
    private User user;

    public Shift(Long id, LocalDateTime createAt, LocalDateTime updateAt, String shiftName, LocalTime shiftStart, LocalTime shiftEnd, String description) {
        super(id, createAt, updateAt);
        this.shiftName = shiftName;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        Description = description;
    }

    public Shift(){
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public LocalTime getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(LocalTime shiftStart) {
        this.shiftStart = shiftStart;
    }

    public LocalTime getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(LocalTime shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
