package fpt.g36.gapms.services;

import java.math.BigDecimal;

public interface CateBrandPriceService {

    BigDecimal getPrice(Long categoryId, Long brandId, boolean isColor);

    BigDecimal getPriceByBrandIdAndCateIdAndIsColor(Long brandId, Long cateId, Boolean isColor);
}
