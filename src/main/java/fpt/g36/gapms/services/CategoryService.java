package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();

    List<Category> getCategoriesByBrandId(Long brandId);

    Category getCategoryById(Long id);


    List<String> getAllCategoryNames();
}
