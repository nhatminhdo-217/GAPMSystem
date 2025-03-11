package fpt.g36.gapms.service;


import fpt.g36.gapms.models.entities.Category;
import fpt.g36.gapms.repositories.CategoryRepository;
import fpt.g36.gapms.services.impls.CategoryServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeAll
    static void beforeAll() {
        System.out.println("CategoryServiceImplTest");
    }
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getAllCategories_success(){

        Category category = new Category();
        category.setId(1L);
        category.setName("40/2-5000m");

        Category category_1 = new Category();
        category_1.setId(2L);
        category_1.setName("50/2-6000m");

        List<Category> list = Arrays.asList(category, category_1);

        when(categoryRepository.findAll()).thenReturn(list);

        List<Category> categories = categoryService.getAllCategories();

        assertEquals(2, categories.size());
        assertEquals("40/2-5000m", categories.get(0).getName());

        verify(categoryRepository, times(1)).findAll();

    }

    @Test
    void getCategoriesByBrandId_Success() {
        // Giả lập danh sách category trả về từ repository
        Long brandId = 1L;
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category A");
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category B");
        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryRepository.findByCateBrandPrices_Brand_Id(brandId)).thenReturn(categories);

        // Gọi phương thức cần kiểm thử
        List<Category> result = categoryService.getCategoriesByBrandId(brandId);

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách category không được trả về null");
        assertEquals(2, result.size(), "Số lượng category không đúng");
        assertEquals("Category A", result.get(0).getName(), "Category đầu tiên không đúng");

        // Kiểm tra gọi repository đúng một lần
        verify(categoryRepository, times(1)).findByCateBrandPrices_Brand_Id(brandId);

    }
    @Test
    void getCategoriesByBrandId_CategoryNotExists_ReturnsEmptyList() {
        // Giả lập sản phẩm không tồn tại
        Long brandId = 1L;

        when(categoryRepository.findByCateBrandPrices_Brand_Id(brandId)).thenReturn(new ArrayList<>());

        // Gọi phương thức cần kiểm thử
        List<Category> result = categoryService.getCategoriesByBrandId(brandId);

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách category không được trả về null");
        assertEquals(0, result.size(), "Danh sách category phải rỗng khi không có category");

        // Kiểm tra gọi repository đúng một lần
        verify(categoryRepository, times(1)).findByCateBrandPrices_Brand_Id(brandId);
    }

    @Test
    void getAllCategoryNames_Success() {
        // Giả lập danh sách tên category từ database
        List<String> expectedCategoryNames = Arrays.asList("Category A", "Category B", "Category C");

        when(categoryRepository.findAllCategoryNames()).thenReturn(expectedCategoryNames);

        // Gọi phương thức cần kiểm thử
        List<String> result = categoryService.getAllCategoryNames();

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách tên category không được trả về null");
        assertEquals(3, result.size(), "Số lượng tên category không đúng");
        assertEquals("Category A", result.get(0), "Tên category đầu tiên không đúng");

        // Kiểm tra categoryRepository.findAllCategoryNames() được gọi đúng một lần
        verify(categoryRepository, times(1)).findAllCategoryNames();
    }

    @Test
    void getCategoryById_CategoryExists_ReturnsCategory() {
        // Giả lập category tồn tại trong database
        Long categoryId = 1L;
        Category expectedCategory = new Category();
        expectedCategory.setId(categoryId);
        expectedCategory.setName("Category A");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

        // Gọi phương thức cần kiểm thử
        Category result = categoryService.getCategoryById(categoryId);

        // Kiểm tra kết quả
        assertNotNull(result, "Category không được trả về null");
        assertEquals("Category A", result.getName(), "Tên category không đúng");

        // Kiểm tra categoryRepository.findById() được gọi đúng một lần
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void getCategoryById_CategoryNotExists_ThrowsException() {
        // Giả lập category không tồn tại
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ RuntimeException khi category không tồn tại
        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryService.getCategoryById(categoryId));
        assertEquals("Category not found", exception.getMessage());

        // Kiểm tra categoryRepository.findById() được gọi đúng một lần
        verify(categoryRepository, times(1)).findById(categoryId);
    }
    @AfterAll
    static void afterAll() {
        System.out.println("CategoryServiceImplTest");
    }
}
