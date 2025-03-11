package fpt.g36.gapms.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.dto.SolutionDTO;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.Solution;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RfqRepository;
import fpt.g36.gapms.repositories.SolutionRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.impls.SolutionServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SolutionServiceImplTest {

    @Mock
    private SolutionRepository solutionRepository;

    @Mock
    private RfqRepository rfqRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private SolutionServiceImpl solutionService;

    private Solution solution;
    private Rfq rfq;
    private User user;

    @BeforeEach
    void setUp() {
        // Setup mock objects
        solution = new Solution();
        solution.setId(1L);
        solution.setIsSent(SendEnum.NOT_SENT);
        solution.setCreateAt(LocalDateTime.now());
        solution.setUpdateAt(LocalDateTime.now());

        rfq = new Rfq();
        rfq.setId(1L);
        rfq.setExpectDeliveryDate(LocalDate.now().plusDays(5));

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
    }

    @Test
    void addSolution_Success() {
        // Giả lập việc tìm thấy Rfq và User
        when(rfqRepository.findById(1L)).thenReturn(Optional.of(rfq));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Giả lập DTO
        SolutionDTO solutionDTO = new SolutionDTO();
        solutionDTO.setReason("Reason");
        solutionDTO.setActualDeliveryDate(LocalDate.now().plusDays(6));
        solutionDTO.setDescription("Description");

        // Giả lập lưu Solution
        when(solutionRepository.save(any(Solution.class))).thenAnswer(invocation -> {
            Solution solutionArg = invocation.getArgument(0);
            solutionArg.setId(1L);  // Set ID to simulate a persisted object
            return solutionArg;     // Return the same object with set ID
        });

        // Gọi phương thức kiểm thử
        Solution result = solutionService.addSolution(1L, 1L, solutionDTO);

        // Kiểm tra kết quả
        assertNotNull(result, "Solution không được trả về null");
        assertEquals("Reason", result.getReason(), "Lý do không đúng");
        assertEquals(SendEnum.NOT_SENT, result.getIsSent(), "Trạng thái không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(solutionRepository, times(1)).save(any(Solution.class));
    }


    @Test
    void addSolution_RfqNotFound() {
        // Giả lập không tìm thấy Rfq
        when(rfqRepository.findById(1L)).thenReturn(Optional.empty());

        // Giả lập DTO
        SolutionDTO solutionDTO = new SolutionDTO();
        solutionDTO.setReason("Reason");
        solutionDTO.setActualDeliveryDate(LocalDate.now().plusDays(6));
        solutionDTO.setDescription("Description");

        // Kiểm tra ngoại lệ khi không tìm thấy Rfq
        RuntimeException exception = assertThrows(RuntimeException.class, () -> solutionService.addSolution(1L, 1L, solutionDTO));
        assertEquals("RFQ not found with id: 1", exception.getMessage());
    }

    @Test
    void updateSolution_Success() {
        // Giả lập rằng Solution đã tồn tại và không có trạng thái SENT
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        // Giả lập DTO
        SolutionDTO solutionDTO = new SolutionDTO();
        solutionDTO.setReason("Updated Reason");
        solutionDTO.setActualDeliveryDate(LocalDate.now().plusDays(6));
        solutionDTO.setDescription("Updated Description");

        // Giả lập Rfq liên kết với Solution
        Rfq rfq = new Rfq();
        rfq.setExpectDeliveryDate(LocalDate.now().plusDays(5)); // Giả lập ngày mong muốn nhận hàng
        solution.setRfq(rfq);

        // Giả lập cập nhật Solution
        when(solutionRepository.save(any(Solution.class))).thenReturn(solution);

        // Gọi phương thức kiểm thử
        Solution updatedSolution = solutionService.updateSolution(1L, solutionDTO);

        // Kiểm tra kết quả
        assertNotNull(updatedSolution, "Solution không được trả về null");
        assertEquals("Updated Reason", updatedSolution.getReason(), "Lý do không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(solutionRepository, times(1)).save(any(Solution.class));
    }

    @Test
    void updateSolution_AlreadySent() {
        // Giả lập đã gửi Solution
        solution.setIsSent(SendEnum.SENT);
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        // Giả lập DTO
        SolutionDTO solutionDTO = new SolutionDTO();
        solutionDTO.setReason("Updated Reason");
        solutionDTO.setActualDeliveryDate(LocalDate.now().plusDays(6));
        solutionDTO.setDescription("Updated Description");

        // Kiểm tra ngoại lệ khi Solution đã được gửi
        RuntimeException exception = assertThrows(RuntimeException.class, () -> solutionService.updateSolution(1L, solutionDTO));
        assertEquals("Solution đã được gửi (submitted), không thể cập nhật.", exception.getMessage());
    }

    @Test
    void submitSolution_Success() {
        // Giả lập việc tìm thấy Solution
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        // Giả lập cập nhật trạng thái
        when(solutionRepository.save(any(Solution.class))).thenReturn(solution);

        // Gọi phương thức kiểm thử
        Solution submittedSolution = solutionService.submitSolution(1L);

        // Kiểm tra kết quả
        assertNotNull(submittedSolution, "Solution không được trả về null");
        assertEquals(SendEnum.SENT, submittedSolution.getIsSent(), "Trạng thái không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(solutionRepository, times(1)).save(any(Solution.class));
    }

    @Test
    void submitSolution_AlreadySubmitted() {
        // Giả lập đã gửi Solution
        solution.setIsSent(SendEnum.SENT);
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        // Kiểm tra ngoại lệ khi Solution đã được gửi
        RuntimeException exception = assertThrows(RuntimeException.class, () -> solutionService.submitSolution(1L));
        assertEquals("Solution đã được gửi trước đó.", exception.getMessage());
    }

    @Test
    void getSolutionsByCreabyID_Success() {
        // Giả lập việc tìm thấy Solutions theo createById
        when(solutionRepository.findAllByCreateBy_Id(1L)).thenReturn(List.of(solution));

        // Gọi phương thức kiểm thử
        List<Solution> result = solutionService.getSolutionsByCreabyID(1L);

        // Kiểm tra kết quả
        assertNotNull(result, "Danh sách Solutions không được trả về null");
        assertEquals(1, result.size(), "Số lượng Solutions không đúng");

        // Kiểm tra phương thức của repository được gọi đúng
        verify(solutionRepository, times(1)).findAllByCreateBy_Id(1L);
    }
}

