package fpt.g36.gapms.services;

import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.dto.SolutionDTO;
import fpt.g36.gapms.models.entities.Solution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SolutionService {
    Solution addSolution(Long rfqId, Long userId, SolutionDTO solutionDTO);
    Solution updateSolution(Long solutionId, SolutionDTO solutionDTO);
    Solution submitSolution(Long solutionId);
    List<Solution> getSolutionsByCreabyID(Long creabyId);
    Solution getSolutionById(Long solutionId);
    List<Solution> getAllSentedAndApproveByUserIDSolutions(Long userId);
    Page<Solution> getAllSentedAndApproveByUserIDSolutions(Long userId, Pageable pageable);
    Page<Solution> getAllSentedSolutions(Pageable pageable);
    Page<Solution> getSolutionsByCreateByIdAndIsSentOrderByRfqDeadline(Long createById, SendEnum sentStatus, Pageable pageable);
    Optional<Solution> findSolutionByIdAndCreateById(Long id, Long createById);
}
