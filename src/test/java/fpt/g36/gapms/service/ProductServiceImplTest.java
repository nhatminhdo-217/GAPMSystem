package fpt.g36.gapms.service;

import fpt.g36.gapms.models.entities.Product;
import fpt.g36.gapms.repositories.ProductRepository;
import fpt.g36.gapms.services.impls.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
    }

    @Test
    void getAllProducts_Success() {
        // Giả lập phương thức findAll để trả về danh sách sản phẩm
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result, "Danh sách sản phẩm không được trả về null");
        assertEquals(1, result.size(), "Số lượng sản phẩm không đúng");
        assertEquals("Test Product", result.get(0).getName(), "Tên sản phẩm không đúng");

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProductNames_Success() {

        List<String> productNames = Arrays.asList("Test Product");
        when(productRepository.findAllProductName()).thenReturn(productNames);

        List<String> result = productService.getAllProductNames();

        assertNotNull(result, "Danh sách tên sản phẩm không được trả về null");
        assertEquals(1, result.size(), "Số lượng tên sản phẩm không đúng");
        assertEquals("Test Product", result.get(0), "Tên sản phẩm không đúng");

        verify(productRepository, times(1)).findAllProductName();
    }

    @Test
    void getProductById_Success() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result, "Sản phẩm không được trả về null");
        assertEquals(1L, result.getId(), "ID sản phẩm không đúng");
        assertEquals("Test Product", result.getName(), "Tên sản phẩm không đúng");

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_NotFound() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Product result = productService.getProductById(1L);

        assertNull(result, "Sản phẩm phải trả về null khi không tìm thấy");

        verify(productRepository, times(1)).findById(1L);
    }
}
