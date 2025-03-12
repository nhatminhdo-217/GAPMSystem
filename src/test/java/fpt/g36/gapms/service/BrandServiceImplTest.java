package fpt.g36.gapms.service;

import fpt.g36.gapms.models.entities.Brand;
import fpt.g36.gapms.models.entities.Product;
import fpt.g36.gapms.repositories.BrandRepository;
import fpt.g36.gapms.services.ProductService;
import fpt.g36.gapms.services.impls.BrandServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceImplTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private BrandServiceImpl brandService;
    @BeforeAll
    static void beforeAll() {
        System.out.println("BrandServiceImplTest");
    }
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBrands_Success() {

        Brand brand_1 = new Brand();
        Brand brand_2 = new Brand();

        brand_1.setId(1L);
        brand_1.setName("Brand 1");

        brand_2.setId(2L);
        brand_2.setName("Brand 2");

        // Giả lập danh sách brand trong database
        List<Brand> expectedBrands = Arrays.asList(
                brand_1,
                brand_2
        );

        when(brandRepository.findAll()).thenReturn(expectedBrands);

        // Gọi phương thức cần kiểm thử
        List<Brand> result = brandService.getAllBrands();

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách brand không được trả về null");
        assertEquals(2, result.size(), "Số lượng brand không đúng");
        assertEquals("Brand 1", result.get(0).getName(), "Brand đầu tiên không đúng");

        // Kiểm tra brandRepository.findAll() được gọi đúng một lần
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    void getBrandsByProductId_ProductExists_ReturnsBrands() {
        // Giả lập product có danh sách brand
        Long productId = 1L;
        Product product = new Product();
        Brand brand_1 = new Brand();
        Brand brand_2 = new Brand();

        brand_1.setId(1L);
        brand_1.setName("Brand 1");

        brand_2.setId(2L);
        brand_2.setName("Brand 2");
        product.setBrands(new HashSet<>(Arrays.asList(brand_1, brand_2)));


        when(productService.getProductById(productId)).thenReturn(product);

        // Gọi phương thức cần kiểm thử
        List<Brand> result = brandService.getBrandsByProductId(productId);

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách brand không được trả về null");
        assertEquals(2, result.size(), "Số lượng brand không đúng");
        assertEquals("Brand 1", result.get(0).getName(), "Brand đầu tiên không đúng");

        // Kiểm tra productService.getProductById() được gọi đúng một lần
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void getBrandsByProductId_ProductNotExists_ReturnsEmptyList() {
        // Giả lập product không tồn tại
        Long productId = 1L;

        when(productService.getProductById(productId)).thenReturn(null);

        // Gọi phương thức cần kiểm thử
        List<Brand> result = brandService.getBrandsByProductId(productId);

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách brand không được trả về null");
        assertEquals(0, result.size(), "Danh sách brand phải rỗng");

        // Kiểm tra productService.getProductById() được gọi đúng một lần
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void getBrandsByProductId_ProductHasNoBrands_ReturnsEmptyList() {
        // Giả lập product không có brand
        Long productId = 1L;
        Product product = new Product();
        product.setBrands(null);

        when(productService.getProductById(productId)).thenReturn(product);

        // Gọi phương thức cần kiểm thử
        List<Brand> result = brandService.getBrandsByProductId(productId);

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách brand không được trả về null");
        assertEquals(0, result.size(), "Danh sách brand phải rỗng khi product không có brand");

        // Kiểm tra productService.getProductById() được gọi đúng một lần
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void getAllBrandNames_Success() {
        // Giả lập danh sách tên brand từ database
        List<String> expectedBrandNames = Arrays.asList("Brand A", "Brand B", "Brand C");

        when(brandRepository.findAllBrandNames()).thenReturn(expectedBrandNames);

        // Gọi phương thức cần kiểm thử
        List<String> result = brandService.getAllBrandNames();

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách tên brand không được trả về null");
        assertEquals(3, result.size(), "Số lượng tên brand không đúng");
        assertEquals("Brand A", result.get(0), "Tên brand đầu tiên không đúng");

        // Kiểm tra brandRepository.findAllBrandNames() được gọi đúng một lần
        verify(brandRepository, times(1)).findAllBrandNames();
    }

    @Test
    void getBrandById_BrandExists_ReturnsBrand() {
        // Giả lập brand tồn tại trong database
        Long brandId = 1L;
        Brand expectedBrand = new Brand();
        expectedBrand.setId(brandId);
        expectedBrand.setName("Brand A");

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(expectedBrand));

        // Gọi phương thức cần kiểm thử
        Brand result = brandService.getBrandById(brandId);

        // Kiểm tra kết quả
        assertNotNull(result, "Brand không được trả về null");
        assertEquals("Brand A", result.getName(), "Tên brand không đúng");

        // Kiểm tra brandRepository.findById() được gọi đúng một lần
        verify(brandRepository, times(1)).findById(brandId);
    }

    @Test
    void getBrandById_BrandNotExists_ThrowsException() {
        // Giả lập brand không tồn tại
        Long brandId = 1L;

        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ RuntimeException khi brand không tồn tại
        RuntimeException exception = assertThrows(RuntimeException.class, () -> brandService.getBrandById(brandId));
        assertEquals("Brand not found", exception.getMessage());

        // Kiểm tra brandRepository.findById() được gọi đúng một lần
        verify(brandRepository, times(1)).findById(brandId);
    }

    @AfterAll
    static void afterAll() {
        System.out.println("BrandServiceImplTest");
    }
}
