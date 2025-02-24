package fpt.g36.gapms.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.util.Objects;

@Embeddable
public class CateBrandPriceId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 4409315324710903732L;

    @NotNull
    @Column(name = "cate_id", nullable = false)
    private long cateId;

    @NotNull
    @Column(name = "brand_id", nullable = false)
    private long brandId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false; // Không cần Hibernate.getClass vì không cần proxy
        CateBrandPriceId that = (CateBrandPriceId) o;
        return Objects.equals(cateId, that.cateId) && Objects.equals(brandId, that.brandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cateId, brandId);
    }

    public CateBrandPriceId() {
    }

    public CateBrandPriceId(long brandId, long cateId) {
        this.brandId = brandId;
        this.cateId = cateId;
    }

    public long getCateId() {
        return cateId;
    }

    public void setCateId(long cateId) {
        this.cateId = cateId;
    }

    public long getBrandId() {
        return brandId;
    }

    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }
}