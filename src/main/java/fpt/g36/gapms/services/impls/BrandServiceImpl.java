package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Brand;
import fpt.g36.gapms.models.entities.Product;
import fpt.g36.gapms.repositories.BrandRepository;
import fpt.g36.gapms.services.BrandService;
import fpt.g36.gapms.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductService productService;
    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public List<Brand> getBrandsByProductId(Long productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu product không tồn tại
        }
        return product.getBrands() != null ? new ArrayList<>(product.getBrands()) : new ArrayList<>(); // Trả về danh sách rỗng nếu không có Brand
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
