package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.Brand;

import java.util.List;

public interface BrandService {
    List<Brand> getAllBrands();

    List<Brand> getBrandsByProductId(Long productId);
}
