package fpt.g36.gapms.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cate_brand_price")
public class CateBrandPrice {
    @EmbeddedId
    private CateBrandPriceId id;

    @MapsId("cateId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cate_id", nullable = false)
    @JsonBackReference
    private Category cate;

    @MapsId("brandId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonBackReference
    private Brand brand;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

//    @NotNull
//    @Column(name = "has_color", nullable = false)
//    private Boolean hasColor = false;

    public CateBrandPrice() {
    }

    public CateBrandPrice(CateBrandPriceId id, Category cate, Brand brand, BigDecimal price) {
        this.id = id;
        this.cate = cate;
        this.brand = brand;
        this.price = price;
    }

    public CateBrandPriceId getId() {
        return id;
    }

    public void setId(CateBrandPriceId id) {
        this.id = id;
    }

    public Category getCate() {
        return cate;
    }

    public void setCate(Category cate) {
        this.cate = cate;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Transient
    public Boolean isColor(){
        return (id != null) ? id.getIsColor() : null;
    }
}