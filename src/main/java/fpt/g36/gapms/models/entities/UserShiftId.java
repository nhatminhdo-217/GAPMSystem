package fpt.g36.gapms.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class UserShiftId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "shift_id")
    private Long shiftId;

    public UserShiftId() {
    };

    public UserShiftId(Long userId, Long shiftId) {
        this.userId = userId;
        this.shiftId = shiftId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }
}