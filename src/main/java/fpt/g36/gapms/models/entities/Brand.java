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
@Table(name = "brand")
public class Brand extends BaseEntity {

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "production_id", nullable = false)
    private Product production;

    @OneToMany(mappedBy = "brand")
    private Set<CateBrandPrice> cateBrandPrices = new LinkedHashSet<>();

    @OneToMany(mappedBy = "brand")
    private Set<RfqDetail> rfqDetails = new LinkedHashSet<>();

    public Brand() {
    }

    public Brand(Long id, LocalDateTime createAt, LocalDateTime updateAt, String name, String description, Product production, Set<CateBrandPrice> cateBrandPrices, Set<RfqDetail> rfqDetails) {
        super(id, createAt, updateAt);
        this.name = name;
        this.description = description;
        this.production = production;
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

    public Product getProduction() {
        return production;
    }

    public void setProduction(Product production) {
        this.production = production;
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