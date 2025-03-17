package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.CateBrandPrice;
import fpt.g36.gapms.models.entities.CateBrandPriceId;
import fpt.g36.gapms.repositories.CateBrandPriceRepository;
import fpt.g36.gapms.services.CateBrandPriceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CateBrandPriceServiceImpl implements CateBrandPriceService {

    private final CateBrandPriceRepository cateBrandPriceRepository;

    public CateBrandPriceServiceImpl(CateBrandPriceRepository cateBrandPriceRepository) {
        this.cateBrandPriceRepository = cateBrandPriceRepository;
    }

    @Override
    public BigDecimal getPrice(Long categoryId, Long brandId, boolean isColor) {

        CateBrandPriceId pk = new CateBrandPriceId(categoryId, brandId, isColor);

        return cateBrandPriceRepository.findById(pk)
                .map(CateBrandPrice::getPrice)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getPriceByBrandIdAndCateIdAndIsColor(Long brandId, Long cateId, boolean isColor) {
        BigDecimal price = cateBrandPriceRepository.findPriceByBrandIdAndCateIdAndIsColor(brandId, cateId, isColor);
        return price != null ? price : BigDecimal.ZERO; // Trả về 0 nếu không tìm thấy
    }
}
