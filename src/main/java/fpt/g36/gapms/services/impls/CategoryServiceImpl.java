package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Category;
import fpt.g36.gapms.repositories.CategoryRepository;
import fpt.g36.gapms.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getCategoriesByBrandId(Long brandId) {
        List<Category> categories = categoryRepository.findByCateBrandPrices_Brand_Id(brandId);
        System.out.println("Categories for brandId " + brandId + ": " + categories.size());
        return categories;
    }
}
