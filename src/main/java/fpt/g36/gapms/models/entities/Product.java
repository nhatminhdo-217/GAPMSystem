package fpt.g36.gapms.models.entities;

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
    private Set<Brand> brands = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<RfqDetail> rfqDetails = new LinkedHashSet<>();

    public Product() {
    }

    public Product(Long id, LocalDateTime createAt, LocalDateTime updateAt, String name, String description, Set<Brand> brands, Set<RfqDetail> rfqDetails) {
        super(id, createAt, updateAt);
        this.name = name;
        this.description = description;
        this.brands = brands;
        this.rfqDetails = rfqDetails;
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
}