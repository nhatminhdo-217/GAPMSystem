package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.SolutionDTO;
import fpt.g36.gapms.models.entities.Solution;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SolutionService {
    Solution addSolution(Long rfqId, Long userId, SolutionDTO solutionDTO);
    Solution updateSolution(Long solutionId, SolutionDTO solutionDTO);
    Solution submitSolution(Long solutionId);
    List<Solution> getSolutionsByCreabyID(Long creabyId);
    Solution getSolutionById(Long solutionId);
    List<Solution> getAllSentedAndApproveByUserIDSolutions(Long userId);
}
