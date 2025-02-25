package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User extends BaseEntity{

    @Column(name = "name", nullable = false, length = 100)
    private String username;

    @Column(name = "password", length = 128)
    private String password;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "is_verified", columnDefinition = "boolean default false")
    private boolean isVerified;

    @Column(name = "avatar", nullable = false, length = 255)
    private String avatar;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean isActive = true;

    public User(Long id, LocalDateTime createAt, LocalDateTime updateAt, String username, String password, String email, String phoneNumber, Role role, boolean isVerified, String avatar, boolean isActive) {
        super(id, createAt, updateAt);
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isVerified = isVerified;
        this.avatar = avatar;
        this.isActive = isActive;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
