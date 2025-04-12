package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.CateBrandPrice;
import fpt.g36.gapms.models.entities.CateBrandPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CateBrandPriceRepository extends JpaRepository<CateBrandPrice, CateBrandPriceId> {

    @Query(value = "SELECT cbp.price FROM cate_brand_price cbp WHERE cbp.brand_id = :brandId AND cbp.cate_id = :cateId AND cbp.is_color = :isColor", nativeQuery = true)
    BigDecimal findPriceByBrandIdAndCateIdAndIsColor(Long brandId, Long cateId, Boolean isColor);
}
