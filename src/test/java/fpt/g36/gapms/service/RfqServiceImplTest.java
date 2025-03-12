package fpt.g36.gapms.service;

import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RfqRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.impls.RfqServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RfqServiceImplTest {

    @Mock
    private RfqRepository rfqRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RfqServiceImpl rfqService;

    private Rfq rfq;
    private User user;

    @BeforeEach
    void setUp() {
        // Setup mock objects
        rfq = new Rfq();
        rfq.setId(1L);
        rfq.setExpectDeliveryDate(LocalDate.now().plusDays(5));
        rfq.setIsApproved(SendEnum.NOT_SENT);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
    }

    @Test
    void saveRfq_Success() {
        // Giả lập lưu Rfq
        when(rfqRepository.save(any(Rfq.class))).thenReturn(rfq);

        // Gọi phương thức kiểm thử
        Rfq result = rfqService.saveRfq(rfq);

        // Kiểm tra kết quả
        assertNotNull(result, "Rfq không được trả về null");
        assertEquals(1L, result.getId(), "ID không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(rfqRepository, times(1)).save(any(Rfq.class));
    }

    @Test
    void getRfqById_Success() {
        // Giả lập tìm thấy Rfq
        when(rfqRepository.findById(1L)).thenReturn(Optional.of(rfq));

        // Gọi phương thức kiểm thử
        Rfq result = rfqService.getRfqById(1L);

        // Kiểm tra kết quả
        assertNotNull(result, "Rfq không được trả về null");
        assertEquals(1L, result.getId(), "ID không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(rfqRepository, times(1)).findById(1L);
    }

    @Test
    void getRfqById_NotFound() {
        // Giả lập không tìm thấy Rfq
        when(rfqRepository.findById(1L)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ khi không tìm thấy Rfq
        RuntimeException exception = assertThrows(RuntimeException.class, () -> rfqService.getRfqById(1L));
        assertEquals("Không tìm thấy mã lô hàng", exception.getMessage());
    }

    @Test
    void submitRfq_Success() {
        // Giả lập Rfq và User
        when(rfqRepository.findById(1L)).thenReturn(Optional.of(rfq));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Giả lập lưu Rfq
        when(rfqRepository.save(any(Rfq.class))).thenReturn(rfq);

        // Gọi phương thức kiểm thử
        Rfq submittedRfq = rfqService.submitRfq(1L, 1L);

        // Kiểm tra kết quả
        assertNotNull(submittedRfq, "Rfq không được trả về null");
        assertEquals(SendEnum.SENT, submittedRfq.getIsApproved(), "Trạng thái không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(rfqRepository, times(1)).save(any(Rfq.class));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void submitRfq_AlreadySubmitted() {
        // Giả lập Rfq đã được gửi trước đó
        rfq.setIsApproved(SendEnum.SENT);
        when(rfqRepository.findById(1L)).thenReturn(Optional.of(rfq));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Kiểm tra ngoại lệ khi Rfq đã được gửi
        RuntimeException exception = assertThrows(RuntimeException.class, () -> rfqService.submitRfq(1L, 1L));
        assertEquals("Rfq đã được gửi trước đó.", exception.getMessage());
    }

    @Test
    void editRfq_Success() {
        // Giả lập việc tìm thấy Rfq
        when(rfqRepository.findById(1L)).thenReturn(Optional.of(rfq));

        // Gọi phương thức kiểm thử để thay đổi ngày giao hàng
        LocalDate newDate = LocalDate.now().plusDays(10); // Giả lập ngày giao hàng mới
        when(rfqRepository.save(any(Rfq.class))).thenAnswer(invocation -> {
            Rfq updatedRfq = invocation.getArgument(0);
            updatedRfq.setExpectDeliveryDate(newDate);
            return updatedRfq;
        });

        // Gọi phương thức kiểm thử
        Rfq updatedRfq = rfqService.editRfq(1L, newDate);

        // Kiểm tra kết quả
        assertNotNull(updatedRfq, "Rfq không được trả về null");
        assertEquals(newDate, updatedRfq.getExpectDeliveryDate(), "Ngày giao hàng không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(rfqRepository, times(1)).save(updatedRfq);
    }

}

