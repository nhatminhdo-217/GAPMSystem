package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.CateBrandPrice;
import fpt.g36.gapms.models.entities.CateBrandPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CateBrandPriceRepository extends JpaRepository<CateBrandPrice, CateBrandPriceId> {

    @Query("SELECT cbp.price FROM CateBrandPrice cbp WHERE cbp.id.brandId = :brandId AND cbp.id.cateId = :cateId AND cbp.id.isColor = :isColor")
    BigDecimal findPriceByBrandIdAndCateIdAndIsColor(Long brandId, Long cateId, boolean isColor);
}
