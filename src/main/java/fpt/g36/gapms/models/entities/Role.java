package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "role")
public class Role extends BaseEntity{

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;

    public Role(Long id, LocalDateTime createAt, LocalDateTime updateAt, String name, String description, List<User> users) {
        super(id, createAt, updateAt);
        this.name = name;
        this.description = description;
        this.users = users;
    }

    public Role() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
