package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category extends BaseEntity {
    @Column(name = "name", length = 100)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "cate")
    private Set<CateBrandPrice> cateBrandPrices = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cate")
    private Set<RfqDetail> rfqDetails = new LinkedHashSet<>();

    public Category() {
    }

    public Category(Long id, LocalDateTime createAt, LocalDateTime updateAt, String name, String description, Set<CateBrandPrice> cateBrandPrices, Set<RfqDetail> rfqDetails) {
        super(id, createAt, updateAt);
        this.name = name;
        this.description = description;
        this.cateBrandPrices = cateBrandPrices;
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

    public Set<CateBrandPrice> getCateBrandPrices() {
        return cateBrandPrices;
    }

    public void setCateBrandPrices(Set<CateBrandPrice> cateBrandPrices) {
        this.cateBrandPrices = cateBrandPrices;
    }

    public Set<RfqDetail> getRfqDetails() {
        return rfqDetails;
    }

    public void setRfqDetails(Set<RfqDetail> rfqDetails) {
        this.rfqDetails = rfqDetails;
    }
}