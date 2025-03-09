package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.SendEnum;
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
import java.util.List;

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
        Rfq rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with id: " + rfqId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (solutionDTO.getActualDeliveryDate().isBefore(rfq.getExpectDeliveryDate())) {
            throw new RuntimeException("Ngày giao hàng dự kiến không thể trước ngày mong muốn nhận hàng (" + rfq.getExpectDeliveryDate() + ").");
        }

        // Tạo và ánh xạ từ DTO sang Entity
        Solution solution = new Solution();
        solution.setReason(solutionDTO.getReason());
        solution.setActualDeliveryDate(solutionDTO.getActualDeliveryDate());
        solution.setDescription(solutionDTO.getDescription());
        solution.setCreateBy(user);
        solution.setRfq(rfq);
        solution.setIsSent(SendEnum.NOT_SENT); // Mặc định là NOT_SENT khi tạo mới
        solution.setCreateAt(LocalDateTime.now());
        solution.setUpdateAt(LocalDateTime.now());

        Solution savedSolution = solutionRepository.save(solution);
        solutionRepository.flush();
        entityManager.refresh(rfq);
        return savedSolution;
    }

    @Override
    @Transactional
    public Solution updateSolution(Long solutionId, SolutionDTO solutionDTO) {
        Solution existingSolution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new RuntimeException("Solution not found with id: " + solutionId));

        // Kiểm tra nếu solution đã được submit thì không cho phép cập nhật
        if (existingSolution.getIsSent() == SendEnum.SENT) {
            throw new RuntimeException("Solution đã được gửi (submitted), không thể cập nhật.");
        }

        if (solutionDTO.getActualDeliveryDate().isBefore(existingSolution.getRfq().getExpectDeliveryDate())) {
            throw new RuntimeException("Ngày giao hàng dự kiến không thể trước ngày mong muốn nhận hàng (" + existingSolution.getRfq().getExpectDeliveryDate() + ").");
        }

        existingSolution.setReason(solutionDTO.getReason());
        existingSolution.setActualDeliveryDate(solutionDTO.getActualDeliveryDate());
        existingSolution.setDescription(solutionDTO.getDescription());
        existingSolution.setUpdateAt(LocalDateTime.now());

        Solution updatedSolution = solutionRepository.save(existingSolution);
        solutionRepository.flush();
        entityManager.refresh(updatedSolution.getRfq());
        return updatedSolution;
    }

    @Override
    @Transactional
    public Solution submitSolution(Long solutionId) {
        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new RuntimeException("Solution not found with id: " + solutionId));

        // Kiểm tra nếu đã gửi thì không cần làm gì thêm
        if (solution.getIsSent() == SendEnum.SENT) {
            throw new RuntimeException("Solution đã được gửi trước đó.");
        }

        // Cập nhật trạng thái thành SENT
        solution.setIsSent(SendEnum.SENT);
        solution.setUpdateAt(LocalDateTime.now());

        Solution submittedSolution = solutionRepository.save(solution);
        solutionRepository.flush();
        entityManager.refresh(submittedSolution.getRfq());
        return submittedSolution;
    }

    @Override
    public List<Solution> getSolutionsByCreabyID(Long creabyId) {
        return solutionRepository.findAllByCreateBy_Id(creabyId);
    }

    @Override
    public Solution getSolutionById(Long solutionId) {
        return solutionRepository.getById(solutionId);
    }

    @Override
    public List<Solution> getAllSentedAndApproveByUserIDSolutions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return solutionRepository.findAllSentAndApprovedByUserId(SendEnum.SENT, userId);
    }


}