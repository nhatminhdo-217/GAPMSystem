package fpt.g36.gapms.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "product")
public class Product extends BaseEntity {

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "production")
    @JsonManagedReference
    private Set<Brand> brands = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private Set<RfqDetail> rfqDetails = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "thread_id")
    private Thread thread;

    public Product() {
    }

    public Product(Long id, LocalDateTime createAt, LocalDateTime updateAt, String name, String description, Set<Brand> brands, Set<RfqDetail> rfqDetails, Thread thread) {
        super(id, createAt, updateAt);
        this.name = name;
        this.description = description;
        this.brands = brands;
        this.rfqDetails = rfqDetails;
        this.thread = thread;
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

    public Set<Brand> getBrands() {
        return brands;
    }

    public void setBrands(Set<Brand> brands) {
        this.brands = brands;
    }

    public Set<RfqDetail> getRfqDetails() {
        return rfqDetails;
    }

    public void setRfqDetails(Set<RfqDetail> rfqDetails) {
        this.rfqDetails = rfqDetails;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}