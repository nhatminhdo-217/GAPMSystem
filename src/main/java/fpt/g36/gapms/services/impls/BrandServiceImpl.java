package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Brand;
import fpt.g36.gapms.repositories.BrandRepository;
import fpt.g36.gapms.repositories.ProductRepository;
import fpt.g36.gapms.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;
    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public List<Brand> getBrandsByProductId(Long productId) {
        return brandRepository.findByProductId(productId);
    }

    @Override
    public List<String> getAllBrandNames() {
        return brandRepository.findAllBrandNames();
    }

    @Override
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found"));
    }
}
