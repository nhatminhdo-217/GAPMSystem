package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.SolutionDTO;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.Solution;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RfqRepository;
import fpt.g36.gapms.repositories.SolutionRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.SolutionService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SolutionServiceImpl implements SolutionService {
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RfqRepository rfqRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public Solution addSolution(Long rfqId, Long userId, SolutionDTO solutionDTO) {
        Rfq rfq;
        try {
            rfq = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("RFQ not found with id: " + rfqId));
            System.err.println("RFQ found: " + rfq.getId() + ", managed: " + rfq.hashCode());
        } catch (Exception e) {
            System.err.println("Error finding RFQ: " + e.getClass().getName() + " - " + e.getMessage());
            throw e; // Ném lại để controller bắt
        }

        User user;
        try {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            System.err.println("User found: " + user.getId() + ", managed: " + user.hashCode());
        } catch (Exception e) {
            System.err.println("Error finding user: " + e.getClass().getName() + " - " + e.getMessage());
            throw e;
        }

        // Tạo và ánh xạ từ DTO sang Entity
        Solution solution = new Solution();
        solution.setReason(solutionDTO.getReason());
        solution.setActualDeliveryDate(solutionDTO.getActualDeliveryDate());
        solution.setDescription(solutionDTO.getDescription());
        solution.setCreateBy(user);
        solution.setRfq(rfq);
        solution.setCreateAt(LocalDateTime.now()); // Đặt thời gian tạo, nếu cần (theo BaseEntity)
        solution.setUpdateAt(LocalDateTime.now()); // Đặt thời gian cập nhật, nếu cần

        // Kiểm tra trạng thái trước khi lưu
        System.err.println("Solution before save: " + solution + ", rfq: " + solution.getRfq().getId() + ", user: " + solution.getCreateBy().getId());

        // Lưu Solution
        Solution savedSolution;
        try {
            savedSolution = solutionRepository.save(solution);
            System.err.println("Solution saved with ID: " + savedSolution.getId() + ", rfq: " + savedSolution.getRfq().getId() + ", user: " + savedSolution.getCreateBy().getId());
        } catch (Exception e) {
            System.err.println("Error saving solution: " + e.getClass().getName() + " - " + e.getMessage());
            throw new RuntimeException("Lỗi khi lưu Solution: " + e.getMessage(), e);
        }
        solutionRepository.flush();
        System.err.println("Session flushed, solution committed to database.");

        entityManager.refresh(rfq);
        System.err.println("RFQ refreshed: " + rfq + ", Solution: " + rfq.getSolution());
        return savedSolution;
    }
}
