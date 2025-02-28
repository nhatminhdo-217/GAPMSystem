package fpt.g36.gapms.services;

import fpt.g36.gapms.models.entities.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    List<String> getAllProductNames();
    Product getProductById(Long id);


}
