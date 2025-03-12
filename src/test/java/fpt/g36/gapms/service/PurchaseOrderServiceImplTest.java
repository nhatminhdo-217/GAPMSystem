package fpt.g36.gapms.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderInfoDTO;
import fpt.g36.gapms.models.dto.purchase_order.PurchaseOrderItemsDTO;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.Quotation;
import fpt.g36.gapms.repositories.PurchaseOrderRepository;
import fpt.g36.gapms.services.impls.PurchaseOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class PurchaseOrderServiceImplTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;

    private PurchaseOrder purchaseOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Tạo đối tượng PurchaseOrder để kiểm thử
        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);
        purchaseOrder.setStatus(BaseEnum.NOT_APPROVED);
    }

    @Test
    void getPurchaseOrderInfoDTOById_Success() {
        // Giả lập trả về PurchaseOrderInfoDTO
        PurchaseOrderInfoDTO infoDTO = new PurchaseOrderInfoDTO();
        when(purchaseOrderRepository.getPurchaseOrderInfoDTOById(1L)).thenReturn(Optional.of(infoDTO));

        // Gọi phương thức kiểm thử
        Optional<PurchaseOrderInfoDTO> result = purchaseOrderService.getPurchaseOrderInfoDTOById(1L);

        // Kiểm tra kết quả
        assertTrue(result.isPresent(), "Kết quả phải có thông tin");
        assertEquals(infoDTO, result.get(), "Thông tin không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(purchaseOrderRepository, times(1)).getPurchaseOrderInfoDTOById(1L);
    }

    @Test
    void getPurchaseOrderInfoDTOById_NotFound() {
        // Giả lập không có kết quả
        when(purchaseOrderRepository.getPurchaseOrderInfoDTOById(1L)).thenReturn(Optional.empty());

        // Gọi phương thức kiểm thử
        Optional<PurchaseOrderInfoDTO> result = purchaseOrderService.getPurchaseOrderInfoDTOById(1L);

        // Kiểm tra kết quả
        assertFalse(result.isPresent(), "Kết quả không được có thông tin");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(purchaseOrderRepository, times(1)).getPurchaseOrderInfoDTOById(1L);
    }

    @Test
    void getPurchaseOrderItemsDTOById_Success() {
        // Giả lập kết quả trả về từ repository
        List<Object[]> result = Collections.singletonList(
                new Object[]{"item1", "item2", "item3", "item4", 1, new BigDecimal("100"), new BigDecimal("200")}
        );



        when(purchaseOrderRepository.getPurchaseOrderItemsDTOById(1L)).thenReturn(result);

        // Gọi phương thức kiểm thử
        List<PurchaseOrderItemsDTO> items = purchaseOrderService.getPurchaseOrderItemsDTOById(1L);

        // Kiểm tra kết quả
        assertNotNull(items, "Danh sách items không được trả về null");
        assertEquals(1, items.size(), "Số lượng items không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(purchaseOrderRepository, times(1)).getPurchaseOrderItemsDTOById(1L);
    }

    @Test
    void updatePurchaseOrderStatus_Success() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));

        when(purchaseOrderRepository.save(purchaseOrder)).thenReturn(purchaseOrder);

        PurchaseOrder updatedOrder = purchaseOrderService.updatePurchaseOrderStatus(1L);

        assertNotNull(updatedOrder, "Cập nhật đơn hàng không thành công");
        assertEquals(BaseEnum.NOT_APPROVED, updatedOrder.getStatus(), "Trạng thái không đúng");

        verify(purchaseOrderRepository, times(1)).save(updatedOrder);
    }


    @Test
    void updatePurchaseOrderStatus_NotFound() {
        // Giả lập phương thức findById để không tìm thấy đơn hàng
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        // Kiểm tra khi không tìm thấy đơn hàng, phương thức sẽ ném ra ngoại lệ
        assertThrows(RuntimeException.class, () -> purchaseOrderService.updatePurchaseOrderStatus(1L),
                "Purchase order not found");

        // Kiểm tra phương thức không gọi save() khi không tìm thấy đơn hàng
        verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));
    }

}


