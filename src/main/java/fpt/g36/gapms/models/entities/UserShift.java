package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_shift")
public class UserShift {
    @EmbeddedId
    private UserShiftId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("shiftId")
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    public UserShift() {
    }

    ;

    public UserShift(UserShiftId id, User user, Shift shift) {
        this.id = id;
        this.user = user;
        this.shift = shift;
    }

    public UserShiftId getId() {
        return id;
    }

    public void setId(UserShiftId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
}
