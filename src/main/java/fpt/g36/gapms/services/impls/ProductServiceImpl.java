package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Product;
import fpt.g36.gapms.repositories.ProductRepository;
import fpt.g36.gapms.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<String> getAllProductNames() {
        return productRepository.findAllProductName();
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null); // Trả về null nếu không tìm thấy
    }
}
